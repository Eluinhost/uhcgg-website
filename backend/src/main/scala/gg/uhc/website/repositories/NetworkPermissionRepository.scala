package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.NetworkPermission
import sangria.execution.deferred.RelationIds

class NetworkPermissionRepository
    extends Repository[NetworkPermission]
    with CanQuery[NetworkPermission]
    with CanQueryByRelations[NetworkPermission] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[NetworkPermission] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT networkid, userid, isadmin FROM network_permissions".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[NetworkPermission]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.networkPermissionByNetworkId, "networkid"),
      simpleRelationFragment(relationIds, Relations.networkPermissionByUserId, "userid")
    )

}
