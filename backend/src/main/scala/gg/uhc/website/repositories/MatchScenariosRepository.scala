package gg.uhc.website.repositories

import gg.uhc.website.model.MatchScenario

class MatchScenariosRepository extends Repository[MatchScenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[MatchScenario] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT matchId, scenarioId FROM match_scenarios".asInstanceOf[Fragment]

  // TODO should this be absorbed into match/scenario repos with joins?

  private[repositories] val getByScenarioIdQuery = generateConnectionQuery(
    relColumn = "scenarioId",
    sortColumn = "matchId"
  )
  private[repositories] val getByMatchIdQuery = generateConnectionQuery(
    relColumn = "matchId",
    sortColumn = "scenarioId"
  )

  val getByScenarioId: ListConnection = genericConnectionList(getByScenarioIdQuery)
  val getByMatchId: ListConnection    = genericConnectionList(getByMatchIdQuery)
}
