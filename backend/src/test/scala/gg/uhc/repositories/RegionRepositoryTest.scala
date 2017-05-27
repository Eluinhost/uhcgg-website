package gg.uhc.repositories

import gg.uhc.website.repositories.RegionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class RegionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "RegionRepository" should "have valid getAllQuery query" in
    check(RegionRepository.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(RegionRepository.getByIdsQuery(NonEmptyList(1, 2)))
}
