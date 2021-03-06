package gg.uhc.website.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.ActorMaterializer
import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.{MaxGraphQlComplexity, MaxGraphQlDepth}
import gg.uhc.website.schema._
import gg.uhc.website.security.RegistrationSession
import io.circe.{Json, JsonObject}
import io.circe.parser.parse
import sangria.ast.Document
import sangria.execution._
import sangria.execution.deferred.DeferredResolver
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer
import sangria.schema.Schema

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.Lens
import scalaz.Scalaz._

case class GraphqlRequest(operationName: Option[String], query: String, variables: Option[Json])

case class QueryTooComplexException(max: Double, acutal: Double)
    extends Exception(s"Query exceeded max complexity of $max, actual $acutal")
case class QueryTooNestedException(max: Int, actual: Int)
    extends Exception(s"Query exceeded max depth of $max, actual $actual")

class GraphqlRoute(
    schema: Schema[SchemaContext, Unit],
    createContext: () ⇒ SchemaContext,
    maxComplexity: Int @@ MaxGraphQlComplexity,
    maxDepth: Int @@ MaxGraphQlDepth)
    extends PartialRoute
    with CustomJsonCodec {
  import sangria.marshalling.circe._

  implicit val system       = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  /**
    * Render the schema once and serve that for each request
    */
  lazy val renderedSchema: String = SchemaRenderer.renderSchema(schema)

  private val rejectionHandler = RejectionHandler.default
  private val logDuration = extractRequestContext.flatMap { ctx ⇒
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    mapResponse { resp ⇒
      val d = System.currentTimeMillis() - start
      system.log.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    } & handleRejections(rejectionHandler)
  }

  val metadataLens: Lens[SchemaContext, QueryMetadata] = Lens.lensu[SchemaContext, QueryMetadata](
    set = (a, value) ⇒ a.copy(metadata = value),
    get = _.metadata
  )

  val schemaComplexityLens: Lens[SchemaContext, Option[Double]] = metadataLens >=> Lens
    .lensu[QueryMetadata, Option[Double]](
      set = (a, value) ⇒ a.copy(complexity = value),
      get = _.complexity
    )

  val schemaDepthLens: Lens[SchemaContext, Option[Int]] = metadataLens >=> Lens.lensu[QueryMetadata, Option[Int]](
    set = (a, value) ⇒ a.copy(depth = value),
    get = _.depth
  )

  /**
    * Used to measure query complexity. Stores the measured complexity in the context and will throw an exception
    * if the max complexity of 1000 is exceeded
    */
  private val complexityReducer = QueryReducer.measureComplexity[SchemaContext] { (complexity, ctx) ⇒
    if (complexity > maxComplexity) throw QueryTooComplexException(maxComplexity, complexity)

    schemaComplexityLens.set(ctx, complexity.some)
  }

  /**
    * Used to measure query depth. Stores the measured depth in the context and will throw an exception if the max
    * depth of 13 is exceeded
    */
  private val depthReducer = QueryReducer.measureDepth[SchemaContext] { (depth, ctx) ⇒
    if (depth > maxDepth) throw QueryTooNestedException(maxDepth, depth)

    schemaDepthLens.set(ctx, depth.some)
  }

  /**
    * Custom exception handler to make sure the right error message is sent to the client for complexity/depth exceeding
    */
  private val exceptionHandler: Executor.ExceptionHandler = {
    case (_, e: QueryTooComplexException)                  ⇒ HandledException(e.getMessage)
    case (_, e: QueryTooNestedException)                   ⇒ HandledException(e.getMessage)
    case (_, e: AuthorisationException)                    ⇒ HandledException(e.getMessage)
    case (_, e: AuthenticationException)                   ⇒ HandledException(e.getMessage)
    case (_, _: RegistrationSession.InvalidTokenException) ⇒ HandledException("Invalid registration token")
  }

  /**
    * Runs the actual GraphQL query and produces a response that can be sent to the client
    * @param ctx the context to run the query under
    * @param query the actual query to run
    * @param operation idk
    * @param variables the variables provided by the user to go alongside the query
    * @return a Future that will resovle to a status code + JSON response to send to the client
    */
  def runQuery(
      ctx: SchemaContext,
      query: Document,
      operation: Option[String],
      variables: JsonObject
    ): Future[(StatusCode, Json)] =
    Executor
      .execute(
        schema = schema,
        queryAst = query,
        userContext = ctx,
        variables = Json.fromJsonObject(variables),
        operationName = operation,
        deferredResolver = DeferredResolver.fetchers(Fetchers.fetchers: _*),
        queryReducers = depthReducer :: complexityReducer :: Nil, // query depth before complexity
        exceptionHandler = exceptionHandler
      )
      .map(StatusCodes.OK → _)
      .recover {
        case error: QueryAnalysisError ⇒ StatusCodes.BadRequest          → error.resolveError
        case error: ErrorWithResolver  ⇒ StatusCodes.InternalServerError → error.resolveError
      }

  /**
    * Some implementations send variables as a string with JSON inside, some as actual JSON.
    * This converts them both to the same thing
    */
  def coerceVariables(vars: Option[Json]): JsonObject =
    vars
      .flatMap {
        case j if j.isObject ⇒
          j.asObject
        case j if j.isString ⇒
          parse(j.asString.get) // get is safe as it is a JString
          .toOption
            .flatMap(_.asObject) // make sure its an object we're getting
        case _ ⇒
          None
      }
      .getOrElse(JsonObject.empty) // if we couldn't parse it use empty vars instead

  def endpoint(query: GraphqlRequest): Route =
    // First try parsing the query
    QueryParser.parse(query.query) match {
      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        complete(
          StatusCodes.BadRequest → Json.obj(
            "syntaxError" → Json.fromString(error.getMessage()),
            "locations" → Json.arr(
              Json.obj(
                "line"   → Json.fromInt(error.originalError.position.line),
                "column" → Json.fromInt(error.originalError.position.column)
              ))
          )
        )
      case Failure(error) ⇒
        // rethrow other errors
        system.log.error(error, "graphql failure")
        throw error
      case Success(ast) ⇒
        // Build a new context for each request
        val ctx = createContext()

        val future = runQuery(
          ctx = ctx,
          query = ast,
          operation = query.operationName,
          variables = coerceVariables(query.variables)
        )

        onComplete(future) {
          case Success(response) ⇒
            complete(response)
          case Failure(ex) ⇒
            system.log.error(ex, "Failure to complete query")
            complete(StatusCodes.BadRequest → "Internal server error")
        }
    }

  override def route: Route =
    pathPrefix("graphql") {
      path("schema") {
        complete(StatusCodes.OK → HttpEntity(ContentTypes.`text/plain(UTF-8)`, renderedSchema))
      } ~ pathEndOrSingleSlash {
        get {
          getFromResource("graphiql.html")
        } ~ post {
          (entity(as[GraphqlRequest]) & logDuration)(endpoint) ~
            complete(StatusCodes.BadRequest → "Incorrect request format")
        }
      }
    }
}
