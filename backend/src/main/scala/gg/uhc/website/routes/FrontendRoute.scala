package gg.uhc.website.routes

import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import play.twirl.api.HtmlFormat

class FrontendRoute extends PartialRoute with TwirlSupport {
  val renderedApp: HtmlFormat.Appendable = html.react()

  val route: Route = pass {
    complete(renderedApp)
  }
}
