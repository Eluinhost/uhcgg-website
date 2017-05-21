package gg.uhc.website.routes

import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import play.twirl.api.HtmlFormat

class FrontendRoute extends PartialRoute with TwirlSupport {
  val renderedApp: HtmlFormat.Appendable = Seq("frontend-opt-bundle.js", "frontend-fastopt-bundle.js")
    .find(name ⇒ getClass.getClassLoader.getResource(s"public/$name") != null)
    .map(name ⇒ s"/assets/$name")
    .map(url ⇒ html.react(url))
    .get

  val route: Route = pass {
    complete(renderedApp)
  }
}
