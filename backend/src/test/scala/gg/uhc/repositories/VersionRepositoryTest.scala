package gg.uhc.repositories

import gg.uhc.website.repositories.VersionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class VersionRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query all") {
    check(VersionRepository.getAllQuery)
  }

  test("query by ids") {
    check(VersionRepository.getByIdsQuery(NonEmptyList(1, 2)))
  }
}
