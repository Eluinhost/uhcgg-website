package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class VersionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new VersionRepository

  "VersionRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))
}
