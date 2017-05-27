package gg.uhc.repositories

import gg.uhc.website.repositories.StyleRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class StyleRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("query all") {
    check(StyleRepository.getAllQuery)
  }

  test("query by ids") {
    check(StyleRepository.getByIdsQuery(NonEmptyList(1, 2)))
  }
}
