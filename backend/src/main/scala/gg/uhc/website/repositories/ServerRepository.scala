package gg.uhc.website.repositories

import gg.uhc.website.model.Server

class ServerRepository extends Repository[Server] with CanQueryByIds[Server] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Server] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, ownerUserId, networkId, name, address, ip, port, location, regionId, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  private[repositories] val getByNetworkIdQuery = generateConnectionQuery(relColumn = "networkId")
  private[repositories] val getByRegionIdQuery = generateConnectionQuery(relColumn = "regionId")
  private[repositories] val getByOwnerUserIdQuery = generateConnectionQuery(relColumn = "ownerUserid")

  val getByNetworkId: ListConnection = genericConnectionList(getByNetworkIdQuery)
  val getByRegionId: ListConnection = genericConnectionList(getByRegionIdQuery)
  val getByOwnerUserId: ListConnection = genericConnectionList(getByOwnerUserIdQuery)
}
