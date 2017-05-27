package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.ScenarioRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ScenarioRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "ScenarioRepository" should "have valid getByIdsQuery query" in
    check(ScenarioRepository.getByIdsQuery(NonEmptyList(1L, 2L)))

  it should "have valid getAllQuery query" in
    check(ScenarioRepository.getAllQuery)

  it should "have valid relationsQuery query" in
    check(
      ScenarioRepository.relationsQuery(
        ownerIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))
      )
    )
}
