package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.MatchRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class MatchRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "MatchRepository" should "have valid getByIdsQuery query" in
    check(MatchRepository.getByIdsQuery(NonEmptyList(1L, 2L)))

  it should "have valid getAllQuery query" in
    check(MatchRepository.getAllQuery)

  it should "have valid relationsQuery" in
    check(MatchRepository.relationsQuery(
      hostIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
      serverIds = Some(NonEmptyList(1L, 2L)),
      styleIds = Some(NonEmptyList(1, 2)),
      versionIds = Some(NonEmptyList(1, 2))
    ))
}
