package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ScenarioRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ScenarioRepository

  "ScenarioRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList("1", "2")))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery("1"))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.scenarioByOwnerId → Seq(UUID.randomUUID().toString, UUID.randomUUID().toString)
        )
      )
    )
}
