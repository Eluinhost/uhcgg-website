package gg.uhc.website.repositories

import gg.uhc.website.model.MatchScenario
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class MatchScenariosRepository
    extends Repository[MatchScenario]
    with CanQuery[MatchScenario]
    with CanQueryByRelations[MatchScenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[MatchScenario] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT matchId, scenarioId FROM match_scenarios".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[MatchScenario]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.matchScenarioByScenarioId, "scenarioId", "uuid"),
      simpleRelationFragment(relationIds, Relations.matchScenarioByMatchId, "matchId", "uuid")
    )
}
