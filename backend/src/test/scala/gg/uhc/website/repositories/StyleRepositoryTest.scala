package gg.uhc.website.repositories

import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class StyleRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new StyleRepository

  "StyleRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList("1", "2")))
}
