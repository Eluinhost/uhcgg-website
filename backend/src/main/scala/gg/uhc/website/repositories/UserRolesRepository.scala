package gg.uhc.website.repositories

import gg.uhc.website.model.UserRole
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

import scalaz.Scalaz._

class UserRolesRepository extends Repository[UserRole] with CanQuery[UserRole] with CanQueryByRelations[UserRole] {
  import doobie.imports._

  override val composite: Composite[UserRole] = implicitly

  private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT userid, roleid FROM user_roles".asInstanceOf[Fragment]

  def forUser(userId: String): ConnectionIO[List[UserRole]] =
    search(userIds = Seq(userId).some)
      .map(_.filter(_.userId == userId))

  def search(userIds: Option[Seq[String]] = None, roleIds: Option[Seq[Int]] = None): ConnectionIO[List[UserRole]] =
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
      simpleRelationFragment(relationIds, Relations.userRoleByUserId, "userid", "uuid"),
      simpleRelationFragment(relationIds, Relations.userRoleByRoleId, "roleid", "int")
    )
}
