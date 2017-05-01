import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("FrontendMain")
object FrontendMain extends js.JSApp {
  import scalacss.ProdDefaults._
  import scalacss.ScalaCssReact._
  import japgolly.scalajs.react.vdom.html_<^._

  @JSExport
  def main(): Unit = {
    println("Application starting")

    GlobalStyles.addToDocument()

    AppComponent.component(<.h1("test")).renderIntoDOM(dom.document.getElementById("root"))
  }
}
