package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import reddit.RedditConfig
import security.CorsSupport
import services.{DatabaseService, UserService}

import scala.concurrent.ExecutionContext

class ApiRoutes(
    redditConfig: RedditConfig,
    db: DatabaseService
  )(implicit actorSystem: ActorSystem,
    ec: ExecutionContext)
    extends HasRoutes
    with CorsSupport {
  import akka.http.scaladsl.server.Directives._

  val userService = new UserService()

  val registerRoutes = new RegisterEndpoints(redditConfig, db, userService)

  val v1: Route = pathPrefix("v1") {
    complete(StatusCodes.NotFound)
  }

  val docs = new SwaggerDocumentationEndpoints()

  override val routes: Route = pathPrefix("api") {
    corsHandler {
      registerRoutes.routes ~ v1 ~ docs.routes ~ complete(StatusCodes.NotFound) // Return 404 for anything else starting /api
    }
  }
}
