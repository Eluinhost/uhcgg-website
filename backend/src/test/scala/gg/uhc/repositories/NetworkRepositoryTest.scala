package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.NetworkRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class NetworkRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query all") {
    check(NetworkRepository.getAllQuery)
  }

  test("query by ids") {
    check(NetworkRepository.getByIdsQuery(NonEmptyList(1L, 2L)))
  }

  test("query by relation") {
    check(NetworkRepository.relationsQuery(ownerIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))))
  }
}
