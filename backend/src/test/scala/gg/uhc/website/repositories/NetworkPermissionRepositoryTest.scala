package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

@DoNotDiscover
class NetworkPermissionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new NetworkPermissionRepository

  "NetworkPermissionRepository" should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.networkPermissionByUserId → Seq(UUID.randomUUID().toString, UUID.randomUUID().toString),
          Relations.networkPermissionByNetworkId → Seq("1", "2")
        )
      )
    )
}
