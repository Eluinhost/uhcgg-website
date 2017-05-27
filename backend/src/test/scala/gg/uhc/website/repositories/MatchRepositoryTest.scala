package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class MatchRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new MatchRepository

  "MatchRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList(1L, 2L)))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(1L))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid relationsQuery" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.matchByHostId    → Seq(UUID.randomUUID(), UUID.randomUUID()),
          Relations.matchByServerId  → Seq(1L, 2L),
          Relations.matchByStyleId   → Seq(1, 2),
          Relations.matchByVersionId → Seq(1, 2)
        )
      )
    )
}
