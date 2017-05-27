package gg.uhc.repositories

import gg.uhc.website.repositories.MatchScenariosRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class MatchScenariosRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by relation") {
    check(
      MatchScenariosRepository.relationsQuery(
        matchIds = Some(NonEmptyList(1L, 2L)),
        scenarioIds = Some(NonEmptyList(1L, 2L))
      ))
  }
}
