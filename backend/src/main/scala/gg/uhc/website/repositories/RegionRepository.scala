package gg.uhc.website.repositories

import gg.uhc.website.model.Region

class RegionRepository
    extends Repository[Region]
    with CanQuery[Region]
    with CanQueryByIds[Region]
    with CanQueryAll[Region] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Region] = implicitly

  private[repositories] val baseSelectQuery: Fragment = fr"SELECT uuid, short, long FROM regions".asInstanceOf[Fragment]
}
