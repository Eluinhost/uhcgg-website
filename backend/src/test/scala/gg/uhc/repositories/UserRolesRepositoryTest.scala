package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.UserRolesRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class UserRolesRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by relation") {
    check(
      UserRolesRepository.relationsQuery(
        userIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
        roleIds = Some(NonEmptyList(1, 2))
      ))
  }
}
