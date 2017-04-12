import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class ApiService(private val actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  import akka.http.scaladsl.server.Directives._

  val routes: Route =
    pathPrefix("v1") {
      new SwaggerDocRoutes(actorSystem).routes
    }
}
