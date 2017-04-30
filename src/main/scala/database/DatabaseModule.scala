package database

import com.softwaremill.macwire.wire
import configuration.ConfigurationModule

trait DatabaseModule extends ConfigurationModule {
  lazy val databaseService: DatabaseService   = wire[DatabaseService]
  lazy val migrationService: MigrationService = wire[MigrationService]
}
