package gg.uhc.website.schema.definitions

import sangria.execution.deferred.Relation
import gg.uhc.website.model._

object Relations {
  val banByBannedUserId = Relation[Ban, String]("banByBannedUserId", ban ⇒ Seq(ban.userId))
  val userRoleByUserId  = Relation[UserRole, String]("roleIdsByUserId", userRole ⇒ Seq(userRole.userId))
  val userRoleByRoleId  = Relation[UserRole, String]("userRoleByRoleId", userRole ⇒ Seq(userRole.roleId))
  val networkByUserId   = Relation[Network, String]("networkByUserId", network ⇒ Seq(network.owner))
  val serverByNetworkId = Relation[Server, String]("serverByNetworkId", server ⇒ Seq(server.networkId))
  val serverByRegionId  = Relation[Server, String]("serverByRegionId", server ⇒ Seq(server.region))
  val matchByHostId     = Relation[Match, String]("matchByHostId", m ⇒ Seq(m.host))
  val matchByServerId   = Relation[Match, String]("matchByServerId", m ⇒ Seq(m.serverId))
  val matchByVersionId  = Relation[Match, String]("matchByVersionId", m ⇒ Seq(m.versionId))
  val matchByStyleId    = Relation[Match, String]("matchByStyleId", m ⇒ Seq(m.styleId))
  val scenarioByOwnerId = Relation[Scenario, String]("scenarioByOwnerId", scenario ⇒ Seq(scenario.owner))
  val matchScenarioByMatchId = Relation[MatchScenario, String](
    "matchScenarioByMatchId",
    matchScenario ⇒ Seq(matchScenario.matchId)
  )
  val matchScenarioByScenarioId = Relation[MatchScenario, String](
    "matchScenarioByScenarioId",
    matchScenario ⇒ Seq(matchScenario.scenarioId)
  )
  val networkPermissionByUserId = Relation[NetworkPermission, String](
    "networkPermissionByUserId",
    networkPermission ⇒ Seq(networkPermission.userId)
  )
  val networkPermissionByNetworkId = Relation[NetworkPermission, String](
    "networkPermissionByNetworkId",
    networkPermission ⇒ Seq(networkPermission.networkId)
  )
}
