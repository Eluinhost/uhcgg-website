import scalacss.internal.mutable.StyleSheet
import scala.language.postfixOps
import scalacss.ProdDefaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style("body")(
    paddingTop(70 px)
  )
}
