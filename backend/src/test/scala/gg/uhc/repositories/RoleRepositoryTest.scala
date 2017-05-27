package gg.uhc.repositories

import gg.uhc.website.repositories.RoleRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class RoleRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query all") {
    check(RoleRepository.getAllQuery)
  }

  test("query by ids") {
    check(RoleRepository.getByIdsQuery(NonEmptyList(1, 2)))
  }
}
