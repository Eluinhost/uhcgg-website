package gg.uhc.repositories

import gg.uhc.website.repositories.ServerRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class ServerRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "ServerRepository" should "have valid relationsQuery query" in
    check(ServerRepository.relationsQuery(networkIds = Some(NonEmptyList(1L, 2L))))
}
