package gg.uhc.website.repositories

import com.softwaremill.macwire.wire
import gg.uhc.website.database.DatabaseModule

trait RepositoriesModule extends DatabaseModule {
  lazy val userRepository: UserRepository                           = wire[UserRepository]
  lazy val roleRepository: RoleRepository                           = wire[RoleRepository]
  lazy val banRepository: BanRepository                             = wire[BanRepository]
  lazy val userRolesRepository: UserRolesRepository                 = wire[UserRolesRepository]
  lazy val regionRepository: RegionRepository                       = wire[RegionRepository]
  lazy val versionRepository: VersionRepository                     = wire[VersionRepository]
  lazy val networkRepository: NetworkRepository                     = wire[NetworkRepository]
  lazy val serverRepository: ServerRepository                       = wire[ServerRepository]
  lazy val styleRepository: StyleRepository                         = wire[StyleRepository]
  lazy val matchRepository: MatchRepository                         = wire[MatchRepository]
  lazy val scenarioRepository: ScenarioRepository                   = wire[ScenarioRepository]
  lazy val matchScenariosRepository: MatchScenariosRepository       = wire[MatchScenariosRepository]
  lazy val networkPermissionRepository: NetworkPermissionRepository = wire[NetworkPermissionRepository]
}
