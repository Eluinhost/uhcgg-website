package gg.uhc.website.repositories

import gg.uhc.website.schema.model.Region

class RegionRepository
    extends Repository[Region]
    with CanQuery[Region]
    with CanQueryByIds[Int, Region]
    with CanQueryAll[Region] {
  import doobie.imports._

  override val composite: Composite[Region] = implicitly
  override val idParam: Param[Int]          = implicitly

  private[repositories] val baseSelectQuery: Fragment = fr"SELECT id, short, long FROM regions".asInstanceOf[Fragment]
}
