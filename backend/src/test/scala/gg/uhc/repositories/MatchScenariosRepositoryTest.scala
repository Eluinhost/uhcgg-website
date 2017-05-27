package gg.uhc.repositories

import gg.uhc.website.repositories.MatchScenariosRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class MatchScenariosRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "MatchScenariosRepository" should "have valid relationsQuery query" in
    check(
      MatchScenariosRepository.relationsQuery(
        matchIds = Some(NonEmptyList(1L, 2L)),
        scenarioIds = Some(NonEmptyList(1L, 2L))
      )
    )
}
