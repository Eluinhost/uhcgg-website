package routes

import akka.http.scaladsl.server.{Directives, Route}

trait PartialRoute extends Directives {
  def route: Route
}
