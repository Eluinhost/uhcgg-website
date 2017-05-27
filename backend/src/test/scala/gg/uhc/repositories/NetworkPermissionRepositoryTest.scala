package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.NetworkPermissionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class NetworkPermissionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "NetworkPermissionRepository" should "have valid relationsQuery query" in
    check(
      NetworkPermissionRepository.relationsQuery(
        userIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
        networkIds = Some(NonEmptyList(1L, 2L))
      )
    )
}
