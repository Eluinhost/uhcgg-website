package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Match
import sangria.execution.deferred.RelationIds

class MatchRepository
    extends Repository[Match]
    with CanQuery[Match]
    with CanQueryByIds[Long, Match]
    with CanQueryAll[Match]
    with CanQueryByRelations[Match] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Match] = implicitly
  override val idParam: Param[Long]        = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, host, serverid, versionid, styleid, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Match]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.matchByHostId, "host"),
      simpleRelationFragment(relationIds, Relations.matchByServerId, "serverid"),
      simpleRelationFragment(relationIds, Relations.matchByStyleId, "styleid"),
      simpleRelationFragment(relationIds, Relations.matchByVersionId, "versionid")
    )
}
