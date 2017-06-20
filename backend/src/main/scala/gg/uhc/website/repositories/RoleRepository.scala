package gg.uhc.website.repositories

import gg.uhc.website.model.Role

class RoleRepository extends Repository[Role] with HasUuidIdColumn[Role] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Role] = implicitly[Composite[Role]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"name",
      p"permissions"
    ) from p"roles"

  override private[repositories] val idColumn = p"uuid"
}
