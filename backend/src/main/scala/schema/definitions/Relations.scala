package schema.definitions

import java.util.UUID

import sangria.execution.deferred.Relation
import schema.model._

object Relations {
  val banByBannedUserId = Relation[Ban, UUID]("banByBannedUserId", ban ⇒ Seq(ban.userId))
  val userRoleByUserId  = Relation[UserRole, UUID]("roleIdsByUserId", userRole ⇒ Seq(userRole.userId))
  val userRoleByRoleId  = Relation[UserRole, Int]("userRoleByRoleId", userRole ⇒ Seq(userRole.roleId))
  val networkByUserId = Relation[Network, UUID]("networkByUserId", network ⇒ Seq(network.owner))
  val serverByNetworkId = Relation[Server, Long]("serverByNetworkId", server ⇒ Seq(server.networkId))
  val matchByHostId = Relation[Match, UUID]("matchByHostId", m ⇒ Seq(m.host))
  val matchByServerId = Relation[Match, Long]("matchByServerId", m ⇒ Seq(m.serverId))
  val matchByVersionId = Relation[Match, Int]("matchByVersionId", m ⇒ Seq(m.versionId))
  val matchByStyleId = Relation[Match, Int]("matchByStyleId", m ⇒ Seq(m.styleId))
  val scenarioByOwnerId = Relation[Scenario, UUID]("scenarioByOwnerId", scenario ⇒ Seq(scenario.owner))
}
