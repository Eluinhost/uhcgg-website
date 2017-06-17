package gg.uhc.website.repositories

import gg.uhc.website.schema.ForwardOnlyConnection
import org.scalatest._

@DoNotDiscover
class NetworkPermissionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new NetworkPermissionRepository

  it should "have valid getByNetworkId query" in
    check(repo.getByNetworkIdQuery(randId, ForwardOnlyConnection(after = Some(randId.toString), first = 10)))
}
