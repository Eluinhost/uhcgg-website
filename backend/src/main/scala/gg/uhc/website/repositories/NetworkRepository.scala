package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.Network

class NetworkRepository extends Repository[Network] with HasUuidIdColumn[Network] with HasRelationColumns[Network] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Network] = implicitly[Composite[Network]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"name",
      p"tag",
      p"description",
      p"created",
      p"modified",
      p"deleted",
      p"owner_user_id"
    ) from p"networks"

  override private[repositories] val idColumn = p"uuid"

  private[repositories] val getByOwnerUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"owner_user_id",
    sort = p"uuid".asc
  )

  val getByOwnerUserId: LookupA[UUID, UUID] = getByOwnerUserIdQuery
}
