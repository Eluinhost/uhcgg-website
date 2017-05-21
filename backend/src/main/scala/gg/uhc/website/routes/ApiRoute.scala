package gg.uhc.website.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class ApiRoute(registerRoute: RegisterRoute, docsRoute: DocumentationRoute, graphql: GraphqlRoute)
    extends PartialRoute {

  override val route: Route = pathPrefix("api") {
    registerRoute.route ~ graphql.route ~ docsRoute.route ~ complete(StatusCodes.NotFound) // Return 404 for anything else starting /api
  }
}
