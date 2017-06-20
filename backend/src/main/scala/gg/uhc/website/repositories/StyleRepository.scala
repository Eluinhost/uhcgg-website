package gg.uhc.website.repositories

import gg.uhc.website.model.Style

class StyleRepository extends Repository[Style] with HasUuidIdColumn[Style] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Style] = implicitly[Composite[Style]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"short_name",
      p"full_name",
      p"description",
      p"requires_size"
    ) from p"styles"

  override private[repositories] val idColumn = p"uuid"
}
