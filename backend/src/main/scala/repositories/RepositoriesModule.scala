package repositories

import com.softwaremill.macwire.wire
import database.DatabaseModule

trait RepositoriesModule extends DatabaseModule {
  lazy val userRepository: UserRepository           = wire[UserRepository]
  lazy val roleRepository: RoleRepository           = wire[RoleRepository]
  lazy val banRepository: BanRepository             = wire[BanRepository]
  lazy val userRolesRepository: UserRolesRepository = wire[UserRolesRepository]
  lazy val regionRepository: RegionRepository       = wire[RegionRepository]
  lazy val versionRepository: VersionRepository     = wire[VersionRepository]
  lazy val networkRepository: NetworkRepository     = wire[NetworkRepository]
  lazy val serverRepository: ServerRepository       = wire[ServerRepository]
}
