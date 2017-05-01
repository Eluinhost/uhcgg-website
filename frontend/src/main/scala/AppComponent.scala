import japgolly.scalajs.react.vdom.html_<^._

object AppComponent {
  import japgolly.scalajs.react._

  class AppComponentBackend($: BackendScope[Unit, Unit]) {
    def render(children: PropsChildren): VdomElement =
      <.div(
        ^.cls := "test",
        children
      )
  }

  val component = ScalaComponent
    .builder[Unit]("AppComponent")
    .stateless
    .renderBackendWithChildren[AppComponentBackend]
    .build
}