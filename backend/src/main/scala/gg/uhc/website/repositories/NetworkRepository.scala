package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Network
import sangria.execution.deferred.RelationIds

class NetworkRepository
    extends Repository[Network]
    with CanQuery[Network]
    with CanQueryByIds[Long, Network]
    with CanQueryAll[Network]
    with CanQueryByRelations[Network] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Network] = implicitly
  override val idParam: Param[Long]          = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, name, tag, description, created, modified, deleted, owner FROM networks".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Network]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.networkByUserId, "owner")
    )
}
