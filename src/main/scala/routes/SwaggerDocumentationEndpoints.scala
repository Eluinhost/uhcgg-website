package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, Route}
import akkahttptwirl.TwirlSupport

class SwaggerDocumentationEndpoints(implicit val actorSystem: ActorSystem)
    extends HasRoutes
    with TwirlSupport
    with Directives {

  val routes: Route =
    (get & pathPrefix("docs")) {
      pathEndOrSingleSlash {
        complete(html.docs.render())
      } ~ pathPrefix("yaml") {
        getFromResourceDirectory("docs")
      }
    }
}
