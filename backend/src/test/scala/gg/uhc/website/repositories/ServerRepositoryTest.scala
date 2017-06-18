package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class ServerRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ServerRepository

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByNetworkId query" in
    check(repo.getByNetworkIdQuery(randId, Some(randId), 10))

  it should "have valid listingQuery query" in
    check(repo.listingQuery(after = Some(randId), limit = 50))
}
