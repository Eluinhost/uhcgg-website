package gg.uhc.website.repositories

import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class RegionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new RegionRepository

  "RegionRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery("1"))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList("1", "2")))
}
