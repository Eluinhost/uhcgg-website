package gg.uhc.website.routes

import akka.http.scaladsl.server.{Directives, Route}
import akkahttptwirl.TwirlSupport

class DocumentationRoute extends PartialRoute with TwirlSupport with Directives {
  val route: Route =
    (get & pathPrefix("docs")) {
      pathEndOrSingleSlash {
        complete(html.docs.render())
      } ~ pathPrefix("yaml") {
        getFromResourceDirectory("docs")
      }
    }
}
