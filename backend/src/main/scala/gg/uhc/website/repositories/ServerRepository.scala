package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.Server

class ServerRepository extends Repository[Server] with HasUuidIdColumn[Server] with HasRelationColumns[Server] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Server] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, ownerUserId, networkId, name, address, ip, port, location, regionId, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  private[repositories] val getByNetworkIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "networkId",
    cursorColumn = "uuid"
  )
  private[repositories] val getByRegionIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "regionId",
    cursorColumn = "uuid"
  )
  private[repositories] val getByOwnerUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "ownerUserId",
    cursorColumn = "uuid"
  )

  val getByNetworkId: LookupA[UUID, UUID]   = getByNetworkIdQuery
  val getByRegionId: LookupA[UUID, UUID]    = getByRegionIdQuery
  val getByOwnerUserId: LookupA[UUID, UUID] = getByOwnerUserIdQuery
}
