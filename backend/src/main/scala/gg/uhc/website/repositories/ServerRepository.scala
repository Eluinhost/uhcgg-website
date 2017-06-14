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
  override implicit val idType: String = "bigint"

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, owner, networkid, name, address, ip, port, location, region, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Server]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.serverByNetworkId, "networkid", "bigint"),
      simpleRelationFragment(relationIds, Relations.serverByRegionId, "region", "int")
    )
}
