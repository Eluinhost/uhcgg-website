package gg.uhc.website.repositories

import gg.uhc.website.model.Network
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class NetworkRepository
    extends Repository[Network]
    with CanQuery[Network]
    with CanQueryByIds[Network]
    with CanQueryAll[Network]
    with CanQueryByRelations[Network] {
  import doobie.imports._

  override val composite: Composite[Network] = implicitly
  override implicit val idType: String = "bigint"

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, name, tag, description, created, modified, deleted, owner FROM networks".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Network]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.networkByUserId, "owner", "uuid")
    )
}
