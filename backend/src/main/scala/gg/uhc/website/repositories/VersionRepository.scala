package gg.uhc.website.repositories

import gg.uhc.website.model.Version

class VersionRepository
    extends Repository[Version]
    with CanQuery[Version]
    with CanQueryByIds[Version]
    with CanQueryAll[Version] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Version] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT uuid, name, live FROM versions".asInstanceOf[Fragment]
}
