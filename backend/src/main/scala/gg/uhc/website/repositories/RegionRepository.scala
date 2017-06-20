package gg.uhc.website.repositories

import gg.uhc.website.model.Region

class RegionRepository extends Repository[Region] with HasUuidIdColumn[Region] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Region] = implicitly[Composite[Region]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"short",
      p"long"
    ) from p"regions"

  override private[repositories] val idColumn = p"uuid"
}
