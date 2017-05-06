package schema.definitions

import java.util.UUID

import sangria.execution.deferred.Relation
import schema.model.{Ban, Network, Server, UserRole}

object Relations {
  val banByBannedUserId = Relation[Ban, UUID]("banByBannedUserId", ban ⇒ Seq(ban.userId))
  val userRoleByUserId  = Relation[UserRole, UUID]("roleIdsByUserId", userRole ⇒ Seq(userRole.userId))
  val userRoleByRoleId  = Relation[UserRole, Int]("userRoleByRoleId", userRole ⇒ Seq(userRole.roleId))
  val networkByUserId = Relation[Network, UUID]("networkByUserId", network ⇒ Seq(network.owner))
  val serverByNetworkId = Relation[Server, Long]("serverByNetworkId", server ⇒ Seq(server.networkId))
}
