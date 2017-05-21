package gg.uhc.website.schema

import gg.uhc.website.repositories._

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
    val networkPermissions: NetworkPermissionRepository)
    extends Mutation {
  var queryComplexity: Option[Double] = None
  var queryDepth: Option[Int]         = None
}
