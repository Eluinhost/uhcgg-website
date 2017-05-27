package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ServerRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new ServerRepository

  "ServerRepository" should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.serverByNetworkId → Seq(1L, 2L)
        )
      )
    )

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(1L))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList(1L, 2L)))
}
