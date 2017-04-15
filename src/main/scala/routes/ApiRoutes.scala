package routes

import akka.http.scaladsl.server.Route

class ApiRoutes() extends HasRoutes {
  import akka.http.scaladsl.server.Directives._

  override val routes: Route = pathPrefix("v1") {
    complete("TODO")
  }
}
