package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.MatchRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class MatchRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by ids") {
    check(MatchRepository.getByIdsQuery(NonEmptyList(1L, 2L)))
  }

  test("query all") {
    check(MatchRepository.getAllQuery)
  }

  test("query by relation") {
    check(MatchRepository.relationsQuery(
      hostIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())),
      serverIds = Some(NonEmptyList(1L, 2L)),
      styleIds = Some(NonEmptyList(1, 2)),
      versionIds = Some(NonEmptyList(1, 2))
    ))
  }

}
