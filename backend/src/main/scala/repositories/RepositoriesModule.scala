package repositories

import com.softwaremill.macwire.wire
import database.DatabaseModule

trait RepositoriesModule extends DatabaseModule {
  lazy val userRepository: UserRepository = wire[UserRepository]
  lazy val roleRepository: RoleRepository = wire[RoleRepository]
}
