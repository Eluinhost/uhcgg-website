package gg.uhc.website.repositories

import gg.uhc.website.model.Version

class VersionRepository extends Repository[Version] with CanQueryByIds[Version] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Version] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, live FROM versions".asInstanceOf[Fragment]
}
