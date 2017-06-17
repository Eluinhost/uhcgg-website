package gg.uhc.website.repositories

import gg.uhc.website.schema.ForwardOnlyConnection
import org.scalatest._

@DoNotDiscover
class ServerRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ServerRepository

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid getByNetworkId query" in
    check(repo.getByNetworkIdQuery(randId, ForwardOnlyConnection(after = Some(randId.toString), first = 10)))
}
