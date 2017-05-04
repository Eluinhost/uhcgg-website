package repositories

import doobie.util.composite.Composite
import doobie.util.fragment

object IdeFixes {
  implicit class SqlIdeFixer(sc: StringContext) extends doobie.syntax.string.SqlInterpolator(sc) {
    def sqlize[A: Composite](args: A): fragment.Fragment   = this.sql.applyProduct(args)
    def fragment[A: Composite](args: A): fragment.Fragment = this.fr.applyProduct(args)
  }
}
