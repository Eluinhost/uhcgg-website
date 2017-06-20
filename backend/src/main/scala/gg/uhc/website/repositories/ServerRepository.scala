package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.Server

class ServerRepository extends Repository[Server] with HasUuidIdColumn[Server] with HasRelationColumns[Server] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Server] = implicitly[Composite[Server]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"owner_user_id",
      p"network_id",
      p"name",
      p"address",
      p"ip",
      p"port",
      p"location",
      p"region_id",
      p"created",
      p"modified",
      p"deleted"
    ) from p"servers"

  override private[repositories] val idColumn = p"uuid"

  private[repositories] val getByNetworkIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"network_id",
    sort = p"uuid".asc
  )
  private[repositories] val getByRegionIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"region_id",
    sort = p"uuid".asc
  )
  private[repositories] val getByOwnerUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"owner_user_id",
    sort = p"uuid".asc
  )

  val getByNetworkId: LookupA[UUID, UUID]   = getByNetworkIdQuery
  val getByRegionId: LookupA[UUID, UUID]    = getByRegionIdQuery
  val getByOwnerUserId: LookupA[UUID, UUID] = getByOwnerUserIdQuery
}
