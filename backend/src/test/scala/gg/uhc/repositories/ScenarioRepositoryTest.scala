package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.ScenarioRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ScenarioRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by ids") {
    check(ScenarioRepository.getByIdsQuery(NonEmptyList(1L, 2L)))
  }

  test("query all") {
    check(ScenarioRepository.getAllQuery)
  }

  test("query relations") {
    check(ScenarioRepository.relationsQuery(
      ownerIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))
    ))
  }
}
