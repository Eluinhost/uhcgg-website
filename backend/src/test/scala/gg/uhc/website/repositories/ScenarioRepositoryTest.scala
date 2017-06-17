package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class ScenarioRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ScenarioRepository

  "ScenarioRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)
}
