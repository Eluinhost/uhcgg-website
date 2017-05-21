package gg.uhc.website.database

import com.softwaremill.macwire.wire
import gg.uhc.website.configuration.ConfigurationModule

trait DatabaseModule extends ConfigurationModule {
  lazy val databaseService: DatabaseService   = wire[DatabaseService]
  lazy val migrationService: MigrationService = wire[MigrationService]
}
