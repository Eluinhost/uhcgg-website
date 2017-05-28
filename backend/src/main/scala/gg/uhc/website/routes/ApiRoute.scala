package gg.uhc.website.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.CorsDirectives

class ApiRoute(registerRoute: RegisterRoute, docsRoute: DocumentationRoute, graphql: GraphqlRoute)
    extends PartialRoute
    with CorsDirectives {

  override val route: Route = (cors() & pathPrefix ("api")) {
    registerRoute.route ~ graphql.route ~ docsRoute.route ~ complete(StatusCodes.NotFound) // Return 404 for anything else starting /api
  }
}
