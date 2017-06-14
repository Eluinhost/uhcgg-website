package gg.uhc.website.schema.definitions

import sangria.execution.deferred.Relation
import gg.uhc.website.model._

object Relations {
  val banByBannedUserId         = Relation("banByBannedUserId", (ban: Ban) ⇒ Seq(ban.bannedUserId))
  val userRoleByUserId          = Relation("roleIdsByUserId", (userRole: UserRole) ⇒ Seq(userRole.userId))
  val userRoleByRoleId          = Relation("userRoleByRoleId", (userRole: UserRole) ⇒ Seq(userRole.roleId))
  val networkByUserId           = Relation("networkByUserId", (network: Network) ⇒ Seq(network.ownerUserId))
  val serverByNetworkId         = Relation("serverByNetworkId", (server: Server) ⇒ Seq(server.networkId))
  val serverByRegionId          = Relation("serverByRegionId", (server: Server) ⇒ Seq(server.regionId))
  val matchByHostId             = Relation("matchByHostId", (m: Match) ⇒ Seq(m.hostUserId))
  val matchByServerId           = Relation("matchByServerId", (m: Match) ⇒ Seq(m.serverId))
  val matchByVersionId          = Relation("matchByVersionId", (m: Match) ⇒ Seq(m.versionId))
  val matchByStyleId            = Relation("matchByStyleId", (m: Match) ⇒ Seq(m.styleId))
  val scenarioByOwnerId         = Relation("scenarioByOwnerId", (scenario: Scenario) ⇒ Seq(scenario.ownerUserId))
  val matchScenarioByMatchId    = Relation("matchScenarioByMatchId", (m: MatchScenario) ⇒ Seq(m.matchId))
  val matchScenarioByScenarioId = Relation("matchScenarioByScenarioId", (m: MatchScenario) ⇒ Seq(m.scenarioId))
  val networkPermissionByUserId = Relation("networkPermissionByUserId", (n: NetworkPermission) ⇒ Seq(n.userId))
  val networkPermissionByNetworkId =
    Relation("networkPermissionByNetworkId", (n: NetworkPermission) ⇒ Seq(n.networkId))
}
