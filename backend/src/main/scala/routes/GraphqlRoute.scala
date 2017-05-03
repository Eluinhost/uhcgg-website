package routes

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import database.DatabaseService
import database.queries.{RoleQueries, UserQueries}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation
import io.circe.{Json, JsonObject}
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.InputUnmarshaller
import sangria.parser.QueryParser
import sangria.renderer.SchemaRenderer
import schema.UserSchemaDefinition.User
import schema._
import schema.context.{SchemaContext, RoleContext, UserContext}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.Scalaz._

case class GraphqlRequest(operationName: Option[String], query: String, variables: Option[Json])

class GraphqlRoute(database: DatabaseService)
    extends PartialRoute
    with UserQueries
    with RoleQueries
    with FailFastCirceSupport
    with AutoDerivation {
  import sangria.marshalling.circe._

  implicit val system       = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  lazy val renderedSchema: String = SchemaRenderer.renderSchema(SchemaDefinition.schema)

  val context = new SchemaContext {
    override val users = new UserContext {
      override def getById(id: UUID): Future[Option[User]] = database.run(getUserById(id))
      override def getByIds(ids: Seq[UUID]): Future[List[User]] = ids.toList.toNel match {
        case Some(nel) ⇒ database.run(getUsersByIds(nel))
        case _         ⇒ Future successful List()
      }

      override def getByUsername(username: String): Future[Option[User]] = database.run(getUserByUsername(username))
      override def getByUsernames(usernames: Seq[String]): Future[List[User]] = usernames.toList.toNel match {
        case Some(nel) ⇒ database.run(getUsersByUsernames(nel))
        case _         ⇒ Future successful List()
      }
    }

    override val roles = new RoleContext {
      override def getRoles: Future[List[RoleSchemaDefinition.Role]] = database.run(getAllRoles)
    }
  }

  def endpoint(query: GraphqlRequest): Future[(StatusCode, Json)] =
    QueryParser.parse(query.query) match {
      // can't parse GraphQL query, return error
      case Failure(error) ⇒
        Future successful (StatusCodes.BadRequest → Json.obj("error" → Json.fromString(error.getMessage)))
      case Success(ast) ⇒
        Executor
          .execute(
            SchemaDefinition.schema,
            ast,
            userContext = context,
            variables =
              InputUnmarshaller.mapVars(query.variables.flatMap(_.asObject).getOrElse(JsonObject.empty).toMap),
            operationName = query.operationName,
            deferredResolver = DeferredResolver.fetchers(SchemaDefinition.fetchers: _*)
          )
          .map(node ⇒ StatusCodes.OK → node)
          .recover {
            case error: QueryAnalysisError ⇒ StatusCodes.BadRequest          → error.resolveError
            case error: ErrorWithResolver  ⇒ StatusCodes.InternalServerError → error.resolveError
          }
    }

  override def route: Route =
    pathPrefix("graphql") {
      pathEndOrSingleSlash {
        get {
          getFromResource("graphiql.html")
        } ~ post {
          entity(as[GraphqlRequest]) { request ⇒
            complete(endpoint(request))
          } ~ complete(StatusCodes.BadRequest → "Incorrect request format")
        }
      } ~ path("schema") {
        complete(StatusCodes.OK → HttpEntity(ContentTypes.`text/plain(UTF-8)`, renderedSchema))
      }
    }
}
