package schema.definitions

import java.util.UUID

import sangria.execution.deferred.Relation
import schema.model.{Ban, Role, UserRole}

object Relations {
  val banByBannedUserId = Relation[Ban, UUID]("banByBannedUserId", ban ⇒ Seq(ban.userId))
  val userRoleByUserId  = Relation[UserRole, UUID]("roleIdsByUserId", userRole ⇒ Seq(userRole.userId))
  val userRoleByRoleId  = Relation[UserRole, Int]("userRoleByRoleId", userRole ⇒ Seq(userRole.roleId))
}
