package gg.uhc.website.repositories

import gg.uhc.website.model.Server
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class ServerRepository
    extends Repository[Server]
    with CanQuery[Server]
    with CanQueryByIds[Server]
    with CanQueryByRelations[Server] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Server] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT uuid, ownerUserId, networkId, name, address, ip, port, location, regionId, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Server]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.serverByNetworkId, "networkId", "uuid"),
      simpleRelationFragment(relationIds, Relations.serverByRegionId, "regionId", "uuid")
    )
}
