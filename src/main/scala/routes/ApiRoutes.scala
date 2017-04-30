package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import security.CorsSupport
import services.UserService

class ApiRoutes(implicit actorSystem: ActorSystem) extends HasRoutes with CorsSupport with Directives {
  val userService = new UserService()

  val registerRoutes = new RegisterEndpoints(userService)

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
