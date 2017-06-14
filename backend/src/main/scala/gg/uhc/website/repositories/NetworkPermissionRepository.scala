package gg.uhc.website.repositories

import gg.uhc.website.model.NetworkPermission
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class NetworkPermissionRepository
    extends Repository[NetworkPermission]
    with CanQuery[NetworkPermission]
    with CanQueryByRelations[NetworkPermission] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[NetworkPermission] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT networkId, userId, isadmin FROM network_permissions".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[NetworkPermission]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.networkPermissionByNetworkId, "networkId", "uuid"),
      simpleRelationFragment(relationIds, Relations.networkPermissionByUserId, "userId", "uuid")
    )

}
