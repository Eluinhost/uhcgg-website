package gg.uhc.website.repositories

import gg.uhc.website.model.Style

class StyleRepository
    extends Repository[Style]
    with CanQuery[Style]
    with CanQueryByIds[Int, Style]
    with CanQueryAll[Style] {
  import doobie.imports._

  override val composite: Composite[Style] = implicitly
  override val idParam: Param[Int]         = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]
}
