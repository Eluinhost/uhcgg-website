package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import security.CorsSupport

class ApiRoute(registerRoute: RegisterRoute, docsRoute: DocumentationRoute)
    extends PartialRoute
    with CorsSupport
    with Directives {

  val v1: Route = pathPrefix("v1") {
    complete(StatusCodes.NotFound)
  }

  override val route: Route = pathPrefix("api") {
    corsHandler {
      registerRoute.route ~ v1 ~ docsRoute.route ~ complete(StatusCodes.NotFound) // Return 404 for anything else starting /api
    }
  }
}
