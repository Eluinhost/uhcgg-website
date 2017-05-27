package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.MatchScenario
import sangria.execution.deferred.RelationIds

class MatchScenariosRepository
    extends Repository[MatchScenario]
    with CanQuery[MatchScenario]
    with CanQueryByRelations[MatchScenario] {
  import doobie.imports._

  override val composite: Composite[MatchScenario] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT matchid, scenarioid FROM match_scenarios".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[MatchScenario]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.matchScenarioByScenarioId, "scenarioid"),
      simpleRelationFragment(relationIds, Relations.matchScenarioByMatchId, "matchid")
    )
}
