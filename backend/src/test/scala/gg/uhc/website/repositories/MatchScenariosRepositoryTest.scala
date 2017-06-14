package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

@DoNotDiscover
class MatchScenariosRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new MatchScenariosRepository

  "MatchScenariosRepository" should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.matchScenarioByMatchId → Seq("1", "2"),
          Relations.matchScenarioByScenarioId → Seq("1", "2")
        )
      )
    )
}
