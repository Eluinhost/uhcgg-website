package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

@DoNotDiscover
class NetworkRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new NetworkRepository

  "NetworkRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))

  it should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.networkByUserId → randIdsSeq
        )
      )
    )
}
