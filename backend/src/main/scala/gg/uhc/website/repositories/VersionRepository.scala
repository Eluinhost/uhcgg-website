package gg.uhc.website.repositories

import gg.uhc.website.schema.model.Version

class VersionRepository
    extends Repository[Version]
    with CanQuery[Version]
    with CanQueryByIds[Int, Version]
    with CanQueryAll[Version] {
  import doobie.imports._

  override val composite: Composite[Version] = implicitly
  override val idParam: Param[Int]           = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, name, live FROM versions".asInstanceOf[Fragment]
}
