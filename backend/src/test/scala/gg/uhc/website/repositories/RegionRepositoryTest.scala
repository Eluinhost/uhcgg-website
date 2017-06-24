package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class RegionRepositoryTest extends BaseRepositoryTest[RegionRepository] {
  val repo = new RegionRepository

  "RegionRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))
}
