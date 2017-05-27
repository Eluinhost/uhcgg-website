package gg.uhc.repositories

import gg.uhc.website.repositories.VersionRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class VersionRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "VersionRepository" should "have valid getAllQuery query" in
    check(VersionRepository.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(VersionRepository.getByIdsQuery(NonEmptyList(1, 2)))
}
