package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

@DoNotDiscover
class ServerRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ServerRepository

  "ServerRepository" should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.serverByNetworkId → randIdsSeq,
          Relations.serverByRegionId → randIdsSeq
        )
      )
    )

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))
}
