package gg.uhc.website.schema

import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.repositories._
import gg.uhc.website.security.{ApiSession, RegistrationSession}

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
    matchScenarios: MatchScenariosRepository,
    networkPermissions: NetworkPermissionRepository,
    registrationSession: RegistrationSession,
    apiSession: ApiSession,
    run: DatabaseRunner,
    metadata: QueryMetadata)
    extends Mutation
