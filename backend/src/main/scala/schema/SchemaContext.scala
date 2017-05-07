package schema

import repositories._

case class SchemaContext(
    users: UserRepository,
    roles: RoleRepository,
    bans: BanRepository,
    userRoles: UserRolesRepository,
    regions: RegionRepository,
    versions: VersionRepository,
    networks: NetworkRepository,
    servers: ServerRepository,
    styles: StyleRepository,
    matches: MatchRepository,
    scenarios: ScenarioRepository,
    matchScenarios: MatchScenariosRepository)
