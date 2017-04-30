package routes

import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import play.twirl.api.HtmlFormat

class FrontendRoute extends PartialRoute with TwirlSupport {
  val renderedApp: HtmlFormat.Appendable = html.react("app")

  val route: Route = pass {
    complete(renderedApp)
  }
}
