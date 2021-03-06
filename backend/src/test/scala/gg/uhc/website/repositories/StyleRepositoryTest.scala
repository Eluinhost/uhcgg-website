package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class StyleRepositoryTest extends BaseRepositoryTest[StyleRepository] {
  val repo = new StyleRepository

  "StyleRepository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))
}
