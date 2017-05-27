package gg.uhc.repositories

import gg.uhc.website.repositories.RoleRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class RoleRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "Role Repository" should "have valid getAllQuery query" in
    check(RoleRepository.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(RoleRepository.getByIdsQuery(NonEmptyList(1, 2)))
}
