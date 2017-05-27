package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.UserRolesRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class UserRolesRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "UserRolesRespository" should "have valid relationsQuery query" in
    check(
      UserRolesRepository.relationsQuery(
        userIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
        roleIds = Some(NonEmptyList(1, 2))
      )
    )
}
