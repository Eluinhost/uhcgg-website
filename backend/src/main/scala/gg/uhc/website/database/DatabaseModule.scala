package gg.uhc.website.database

import javax.sql.DataSource

import com.softwaremill.macwire._
import com.softwaremill.tagging.@@
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.hikaritransactor.HikariTransactor
import doobie.imports._
import gg.uhc.website.configuration.{ConfigurationModule, DatabaseConnectionStringConfig, DatabasePasswordConfig, DatabaseUsernameConfig}
import org.flywaydb.core.Flyway

import scalaz.concurrent.Task

object DatabaseModule {
  def createHikariConfig(
      connectionString: String @@ DatabaseConnectionStringConfig,
      username: String @@ DatabaseUsernameConfig,
      password: String @@ DatabasePasswordConfig
    ): HikariConfig = {
    val config = new HikariConfig()

    config.setJdbcUrl(connectionString)
    config.setUsername(username)
    config.setPassword(password)

    config
  }

  def createHikariDataSource(config: HikariConfig)                     = new HikariDataSource(config)
  def createTransactor(dataSource: HikariDataSource): Transactor[Task] = HikariTransactor[Task](dataSource)

  def createFlyway(source: DataSource): Flyway = {
    val flyway = new Flyway()
    flyway.setDataSource(source)
    flyway
  }
}

trait DatabaseModule extends ConfigurationModule {
  lazy val hikariConfig: HikariConfig     = wireWith(DatabaseModule.createHikariConfig _)
  lazy val dataSource: HikariDataSource   = wireWith(DatabaseModule.createHikariDataSource _)
  lazy val transactor: Transactor[Task]   = wireWith(DatabaseModule.createTransactor _)
  lazy val migrations: Flyway             = wireWith(DatabaseModule.createFlyway _)
  lazy val databaseRunner: DatabaseRunner = wire[DatabaseRunner]
}
