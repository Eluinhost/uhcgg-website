package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

@DoNotDiscover
class UserRolesRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new UserRolesRepository

  "UserRolesRespository" should "have valid relationsQuery query" in
    check(
      repo.relationsQuery(
        Seq(
          Relations.userRoleByRoleId → Seq("1", "2"),
          Relations.userRoleByUserId → Seq(UUID.randomUUID().toString, UUID.randomUUID().toString)
        )
      )
    )
}
