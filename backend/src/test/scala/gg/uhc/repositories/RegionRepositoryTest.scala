package gg.uhc.repositories

import gg.uhc.website.repositories.RegionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class RegionRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query all") {
    check(RegionRepository.getAllQuery)
  }

  test("query by ids") {
    check(RegionRepository.getByIdsQuery(NonEmptyList(1, 2)))
  }
}
