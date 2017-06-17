package gg.uhc.website.repositories

import gg.uhc.website.model.NetworkPermission

class NetworkPermissionRepository extends Repository[NetworkPermission] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[NetworkPermission] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT networkId, userId, isadmin FROM network_permissions".asInstanceOf[Fragment]

  private[repositories] val getByNetworkIdQuery = generateConnectionQuery(
    relColumn = "networkId",
    sortColumn = "userId"
  )
  private[repositories] val getByUserIdQuery = generateConnectionQuery(
    relColumn = "userId",
    sortColumn = "networkId"
  )

  val getByNetworkId: ListConnection = genericConnectionList(getByNetworkIdQuery)
  val getByUserId: ListConnection    = genericConnectionList(getByUserIdQuery)
}
