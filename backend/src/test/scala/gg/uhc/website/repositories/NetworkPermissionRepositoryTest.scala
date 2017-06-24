package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class NetworkPermissionRepositoryTest extends BaseRepositoryTest[NetworkPermissionRepository] {
  val repo = new NetworkPermissionRepository

  it should "have valid getByNetworkId query" in
    check(repo.getByNetworkIdQuery(RelationshipListingParameters(Some(randId), 10, randId)))
}
