package routes
import akka.http.scaladsl.server.Route

class BaseRoute(assetsRoute: AssetsRoute, apiRoute: ApiRoute, frontendRoute: FrontendRoute) extends PartialRoute {
  override def route: Route =
    // check for resources first
    assetsRoute.route ~
      // check for api endpoint
      apiRoute.route ~
      // If it doesn't match we want to send it to the frontend to handle
      frontendRoute.route
}
