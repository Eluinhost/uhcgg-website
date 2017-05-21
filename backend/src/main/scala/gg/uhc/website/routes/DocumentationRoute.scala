package gg.uhc.website.routes

import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport

class DocumentationRoute extends PartialRoute with TwirlSupport {
  val route: Route =
    (get & pathPrefix("docs")) {
      pathEndOrSingleSlash {
        complete(html.docs.render())
      } ~ pathPrefix("yaml") {
        getFromResourceDirectory("docs")
      }
    }
}
