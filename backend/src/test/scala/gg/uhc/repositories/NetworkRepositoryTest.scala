package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.NetworkRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class NetworkRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "NetworkRepository" should "have valid getAllQuery query" in
    check(NetworkRepository.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(NetworkRepository.getByIdsQuery(NonEmptyList(1L, 2L)))

  it should "have valid relationsQuery query" in
    check(NetworkRepository.relationsQuery(ownerIds = Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))))
}
