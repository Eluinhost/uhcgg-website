package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport

class SwaggerDocumentationEndpoints(implicit val actorSystem: ActorSystem) extends HasRoutes with TwirlSupport {
  import akka.http.scaladsl.server.Directives._

  val routes: Route =
    (get & pathPrefix("docs")) {
      pathEndOrSingleSlash {
        complete(html.docs.render())
      } ~ pathPrefix("yaml") {
        getFromResourceDirectory("docs")
      }
    }
}
