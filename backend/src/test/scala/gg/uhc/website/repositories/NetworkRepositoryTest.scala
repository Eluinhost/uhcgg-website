package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class NetworkRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new NetworkRepository

  "NetworkRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(1L))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList(1L, 2L)))

  it should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.networkByUserId → Seq(UUID.randomUUID(), UUID.randomUUID())
        )
      )
    )
}
