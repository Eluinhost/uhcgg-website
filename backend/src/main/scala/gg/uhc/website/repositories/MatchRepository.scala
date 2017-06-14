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

  override val composite: Composite[Match] = implicitly
  override implicit val idType: String = "bigint"

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, host, serverid, versionid, styleid, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Match]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.matchByHostId, "host", "uuid"),
      simpleRelationFragment(relationIds, Relations.matchByServerId, "serverid", "bigint"),
      simpleRelationFragment(relationIds, Relations.matchByStyleId, "styleid", "int"),
      simpleRelationFragment(relationIds, Relations.matchByVersionId, "versionid", "int")
    )
}
