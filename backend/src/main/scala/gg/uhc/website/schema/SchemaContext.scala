package gg.uhc.website.schema

import com.softwaremill.tagging.@@
import gg.uhc.website.configuration.JwtSecret
import gg.uhc.website.repositories._
import pdi.jwt.algorithms.JwtHmacAlgorithm

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
    jwtSecret: String @@ JwtSecret,
    jwtAlgorithm: JwtHmacAlgorithm,
    metadata: QueryMetadata)
    extends Mutation
