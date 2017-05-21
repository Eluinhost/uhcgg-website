package gg.uhc.website.schema

import com.softwaremill.tagging.@@
import gg.uhc.website.configuration.JwtSecret
import gg.uhc.website.repositories._
import pdi.jwt.JwtAlgorithm

class SchemaContext(
    val users: UserRepository,
    val roles: RoleRepository,
    val bans: BanRepository,
    val userRoles: UserRolesRepository,
    val regions: RegionRepository,
    val versions: VersionRepository,
    val networks: NetworkRepository,
    val servers: ServerRepository,
    val styles: StyleRepository,
    val matches: MatchRepository,
    val scenarios: ScenarioRepository,
    val matchScenarios: MatchScenariosRepository,
    val networkPermissions: NetworkPermissionRepository,
    val jwtSecret: String @@ JwtSecret,
    val jwtAlgorithm: JwtAlgorithm)
    extends Mutation {
  var queryComplexity: Option[Double] = None
  var queryDepth: Option[Int]         = None
}
