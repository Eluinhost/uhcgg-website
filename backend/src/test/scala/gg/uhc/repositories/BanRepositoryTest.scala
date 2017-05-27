package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.BanRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class BanRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query by ids") {
    check(BanRepository.getBansByIdsQuery(NonEmptyList(1, 2)))
  }

  test("query all showing expired") {
    check(BanRepository.getAllBansQuery(true))
  }

  test("query all not showing expired") {
    check(BanRepository.getAllBansQuery(false))
  }

  test("query by user ids") {
    check(BanRepository.relationQuery(Some(NonEmptyList(UUID.randomUUID(), UUID.randomUUID()))))
  }
}
