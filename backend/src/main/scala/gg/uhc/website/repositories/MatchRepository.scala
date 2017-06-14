package gg.uhc.website.repositories

import gg.uhc.website.model.Match
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class MatchRepository
    extends Repository[Match]
    with CanQuery[Match]
    with CanQueryByIds[Match]
    with CanQueryAll[Match]
    with CanQueryByRelations[Match] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Match] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT uuid, hostUserId, serverId, versionId, styleId, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Match]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.matchByHostId, "hostUserId", "uuid"),
      simpleRelationFragment(relationIds, Relations.matchByServerId, "serverId", "uuid"),
      simpleRelationFragment(relationIds, Relations.matchByStyleId, "styleId", "uuid"),
      simpleRelationFragment(relationIds, Relations.matchByVersionId, "versionId", "uuid")
    )
}
