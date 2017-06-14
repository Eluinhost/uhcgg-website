package gg.uhc.website.repositories

import gg.uhc.website.model.Style

class StyleRepository
    extends Repository[Style]
    with CanQuery[Style]
    with CanQueryByIds[Style]
    with CanQueryAll[Style] {
  import doobie.imports._

  override val composite: Composite[Style] = implicitly
  override implicit val idType: String = "int"

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]
}
