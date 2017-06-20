package gg.uhc.website.repositories

import gg.uhc.website.model.Version

class VersionRepository extends Repository[Version] with HasUuidIdColumn[Version] {
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Version] = implicitly[Composite[Version]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"name",
      p"live"
    ) from p"versions"

  override private[repositories] val idColumn = p"uuid"
}
