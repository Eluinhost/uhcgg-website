package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.MatchScenario

class MatchScenariosRepository extends Repository[MatchScenario] with HasRelationColumns[MatchScenario] {
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[MatchScenario] = implicitly[Composite[MatchScenario]]

  override private[repositories] val baseSelect =
    select (
      p"match_id",
      p"scenario_id"
    ) from p"match_scenarios"

  // TODO should this be absorbed into match/scenario repos with joins?

  private[repositories] val getByScenarioIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"scenario_id",
    sort = p"match_id".asc
  )
  private[repositories] val getByMatchIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"match_id",
    sort = p"scenario_id".asc
  )

  val getByScenarioId: LookupA[UUID, UUID] = getByScenarioIdQuery
  val getByMatchId: LookupA[UUID, UUID]    = getByMatchIdQuery
}
