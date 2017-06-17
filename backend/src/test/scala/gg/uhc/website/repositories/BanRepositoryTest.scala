package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class BanRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new BanRepository

  "BanRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have a valid getByExpiredStatusQuery query" in
    check(repo.getByExpiredStatusQuery(true))
}
