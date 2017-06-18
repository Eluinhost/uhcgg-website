package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.MatchScenario

class MatchScenariosRepository extends Repository[MatchScenario] with HasRelationColumns[MatchScenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[MatchScenario] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT matchId, scenarioId FROM match_scenarios".asInstanceOf[Fragment]

  // TODO should this be absorbed into match/scenario repos with joins?

  private[repositories] val getByScenarioIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "scenarioId",
    cursorColumn = "matchId"
  )
  private[repositories] val getByMatchIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "matchId",
    cursorColumn = "scenarioId"
  )

  val getByScenarioId: LookupA[UUID, UUID] = getByScenarioIdQuery
  val getByMatchId: LookupA[UUID, UUID]    = getByMatchIdQuery
}
