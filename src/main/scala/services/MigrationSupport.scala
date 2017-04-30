package services

import org.flywaydb.core.Flyway

trait MigrationSupport { self: DatabaseSupport ⇒
  val migrations = new Flyway()
  migrations.setDataSource(dataSource)
}
