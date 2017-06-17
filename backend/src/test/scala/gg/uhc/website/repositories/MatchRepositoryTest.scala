package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class MatchRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new MatchRepository

  "MatchRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)
}
