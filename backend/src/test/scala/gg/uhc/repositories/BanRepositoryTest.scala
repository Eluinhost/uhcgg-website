package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.BanRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class BanRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "BanRepository" should "have valid getBansByIdsQuery query" in
    check(BanRepository.getBansByIdsQuery(NonEmptyList(1, 2)))

  it should "have valid getAllBansQuery query" in
    check(BanRepository.getAllBansQuery(true))

  it should "have valid relationQuery query" in
    check(BanRepository.relationQuery(Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))))
}
