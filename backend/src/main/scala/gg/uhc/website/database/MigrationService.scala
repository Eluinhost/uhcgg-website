package gg.uhc.website.database

import org.flywaydb.core.Flyway

class MigrationService(databaseService: DatabaseService) extends Flyway {
  setDataSource(databaseService.dataSource)
}