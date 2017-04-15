package routes

import akka.http.scaladsl.server.Route

trait HasRoutes {
  val routes: Route
}
