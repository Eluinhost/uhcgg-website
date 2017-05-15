package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation
import io.circe.{Json, JsonObject}
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.InputUnmarshaller
import sangria.parser.QueryParser
import sangria.renderer.SchemaRenderer
import schema.SchemaContext
import schema.definitions.Types.SchemaType
import schema.definitions.Fetchers

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class GraphqlRequest(operationName: Option[String], query: String, variables: Option[Json])

class GraphqlRoute(context: SchemaContext)
    extends PartialRoute
    with FailFastCirceSupport
    with AutoDerivation {
  import sangria.marshalling.circe._

  implicit val system       = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  lazy val renderedSchema: String = SchemaRenderer.renderSchema(SchemaType)

  val rejectionHandler = RejectionHandler.default
  val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      system.log.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    } & handleRejections(rejectionHandler)
  }

  def endpoint(query: GraphqlRequest): Future[(StatusCode, Json)] =
    QueryParser.parse(query.query) match {
      // can't parse GraphQL query, return error
      case Failure(error) ⇒
        Future successful (StatusCodes.BadRequest → Json.obj("error" → Json.fromString(error.getMessage)))
      case Success(ast) ⇒
        Executor
          .execute(
            schema = SchemaType,
            queryAst = ast,
            userContext = context,
            variables = InputUnmarshaller.mapVars(
              query.variables
                .flatMap(_.asObject)
                .getOrElse(JsonObject.empty)
                .toMap
            ),
            operationName = query.operationName,
            deferredResolver = DeferredResolver.fetchers(Fetchers.fetchers: _*)
          )
          .map(StatusCodes.OK → _)
          .recover {
            case error: QueryAnalysisError ⇒ StatusCodes.BadRequest          → error.resolveError
            case error: ErrorWithResolver  ⇒ StatusCodes.InternalServerError → error.resolveError
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
          (entity(as[GraphqlRequest]) & logDuration) { request ⇒
            complete(endpoint(request))
          } ~ complete(StatusCodes.BadRequest → "Incorrect request format")
        }
      }
    }
}
