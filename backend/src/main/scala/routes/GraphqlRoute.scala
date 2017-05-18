package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation
import io.circe.{Json, JsonObject}
import sangria.ast.Document
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError, QueryReducer}
import sangria.marshalling.InputUnmarshaller
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer
import schema.SchemaContext
import schema.definitions.Fetchers
import schema.definitions.Types.SchemaType

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class GraphqlRequest(operationName: Option[String], query: String, variables: Option[Json])

class GraphqlRoute(createContext: () ⇒ SchemaContext)
    extends PartialRoute
    with FailFastCirceSupport
    with AutoDerivation {
  import sangria.marshalling.circe._

  implicit val system       = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  lazy val renderedSchema: String = SchemaRenderer.renderSchema(SchemaType)

  private val rejectionHandler = RejectionHandler.default
  private val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      system.log.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    } & handleRejections(rejectionHandler)
  }

  private val complexityReducer = QueryReducer.measureComplexity[SchemaContext] { (complexity, ctx) ⇒
    ctx.queryComplexity = Some(complexity)
    ctx
  }

  private val depthReducer = QueryReducer.measureDepth[SchemaContext] { (depth, ctx) ⇒
    ctx.queryDepth = Some(depth)
    ctx
  }

  def runQuery(
      ctx: SchemaContext,
      query: Document,
      operation: Option[String],
      variables: Option[Json]
    ): Future[(StatusCode, Json)] =
    Executor
      .execute(
        schema = SchemaType,
        queryAst = query,
        userContext = ctx,
        variables = InputUnmarshaller.mapVars(
          variables
            .flatMap(_.asObject)
            .getOrElse(JsonObject.empty)
            .toMap
        ),
        operationName = operation,
        deferredResolver = DeferredResolver.fetchers(Fetchers.fetchers: _*),
        queryReducers = complexityReducer :: depthReducer :: Nil
      )
      .map { result ⇒
        StatusCodes.OK → result
      }
      .recover {
        case error: QueryAnalysisError ⇒ StatusCodes.BadRequest          → error.resolveError
        case error: ErrorWithResolver  ⇒ StatusCodes.InternalServerError → error.resolveError
      }

  def endpoint(query: GraphqlRequest): Route =
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
        system.log.error(error, "graphql failure")
        throw error
      case Success(ast) ⇒
        val ctx = createContext()

        val future = runQuery(
          ctx = ctx,
          query = ast,
          operation = query.operationName,
          variables = query.variables
        )

        onComplete(future) {
          case Success((statusCode, response)) ⇒
              complete(
                statusCode → response.asObject.get
                  .add("depth", Json.fromInt(ctx.queryDepth.getOrElse(-1)))
                  .add("complexity", Json.fromDouble(ctx.queryComplexity.getOrElse(-1D)).get)
              )
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
          (entity(as[GraphqlRequest]) & logDuration)(endpoint) ~ complete(
            StatusCodes.BadRequest → "Incorrect request format")
        }
      }
    }
}
