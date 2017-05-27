package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.NetworkPermissionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class NetworkPermissionRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by relation") {
    check(
      NetworkPermissionRepository.relationsQuery(
        userIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
        networkIds = Some(NonEmptyList(1L, 2L))
      ))
  }
}
