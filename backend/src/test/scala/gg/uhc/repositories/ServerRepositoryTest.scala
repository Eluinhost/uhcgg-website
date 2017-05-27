package gg.uhc.repositories

import gg.uhc.website.repositories.ServerRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ServerRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by relation") {
    check(ServerRepository.relationsQuery(networkIds = Some(NonEmptyList(1L, 2L))))
  }
}
