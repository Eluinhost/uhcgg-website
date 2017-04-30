package routes
import akka.http.scaladsl.server.Route

class ResourcesRoute extends PartialRoute {
  override def route: Route = pathPrefix("resources") {
    getFromDirectory("build")
  }
}
