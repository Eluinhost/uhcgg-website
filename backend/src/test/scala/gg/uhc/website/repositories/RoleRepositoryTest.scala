package gg.uhc.website.repositories

import org.scalatest._

@DoNotDiscover
class RoleRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new RoleRepository

  "Role Repository" should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(randId))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(randIds))
}
