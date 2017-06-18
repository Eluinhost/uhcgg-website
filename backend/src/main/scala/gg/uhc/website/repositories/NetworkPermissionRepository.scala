package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.NetworkPermission

class NetworkPermissionRepository extends Repository[NetworkPermission] with CanQueryRelations[NetworkPermission] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[NetworkPermission] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT networkId, userId, isadmin FROM network_permissions".asInstanceOf[Fragment]

  private[repositories] val getByNetworkIdQuery = connectionQuery[UUID, UUID](
    relColumn = "networkId",
    cursorColumn = "userId"
  )

  private[repositories] val getByUserIdQuery = connectionQuery[UUID, UUID](
    relColumn = "userId",
    cursorColumn = "networkId"
  )

  val getByNetworkId: LookupA[UUID, UUID] = getByNetworkIdQuery
  val getByUserId: LookupA[UUID, UUID] = getByUserIdQuery
}
