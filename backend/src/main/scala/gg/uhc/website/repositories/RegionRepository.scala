package gg.uhc.website.repositories

import gg.uhc.website.model.Region

class RegionRepository
    extends Repository[Region]
    with CanQuery[Region]
    with CanQueryByIds[Region]
    with CanQueryAll[Region] {
  import doobie.imports._

  override val composite: Composite[Region] = implicitly
  override implicit val idType: String = "int"

  private[repositories] val baseSelectQuery: Fragment = fr"SELECT id, short, long FROM regions".asInstanceOf[Fragment]
}
