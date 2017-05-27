package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.UserRole
import sangria.execution.deferred.RelationIds

class UserRolesRepository extends Repository[UserRole] with CanQuery[UserRole] with CanQueryByRelations[UserRole] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[UserRole] = implicitly

  private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT userid, roleid FROM user_roles".asInstanceOf[Fragment]

  def forUser(userId: UUID): ConnectionIO[List[UserRole]] =
    search(userIds = Some(Seq(userId))).map(_.filter(_.userId == userId))

  def search(userIds: Option[Seq[UUID]] = None, roleIds: Option[Seq[Int]] = None): ConnectionIO[List[UserRole]] =
    relationsQuery(
      buildRelationIds(
        Map(
          Relations.userRoleByUserId → userIds,
          Relations.userRoleByRoleId → roleIds
        )
      )
    ).list

  override def relationsFragment(relationIds: RelationIds[UserRole]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.userRoleByUserId, "userid"),
      simpleRelationFragment(relationIds, Relations.userRoleByRoleId, "roleid")
    )
}
