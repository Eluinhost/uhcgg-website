package gg.uhc.website.repositories

import gg.uhc.website.model.Style

class StyleRepository extends Repository[Style] with CanQueryByIds[Style] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Style] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]
}
