package gg.uhc.repositories

import gg.uhc.website.repositories.StyleRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class StyleRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "StyleRepository" should "have valid getAllQuery query" in
    check(StyleRepository.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(StyleRepository.getByIdsQuery(NonEmptyList(1, 2)))
}
