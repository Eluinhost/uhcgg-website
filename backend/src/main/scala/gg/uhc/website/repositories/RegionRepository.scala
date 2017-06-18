package gg.uhc.website.repositories

import gg.uhc.website.model.Region

class RegionRepository extends Repository[Region] with HasUuidIdColumn[Region] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Region] = implicitly

  private[repositories] val select: Fragment = fr"SELECT uuid, short, long FROM regions".asInstanceOf[Fragment]
}
