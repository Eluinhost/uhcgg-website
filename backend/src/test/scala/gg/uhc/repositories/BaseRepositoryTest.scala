package gg.uhc.repositories

import javax.sql.DataSource

import doobie.scalatest.QueryChecker
import doobie.util.iolite.IOLite
import doobie.util.transactor.{DataSourceTransactor, Transactor}
import gg.uhc.website.configuration.ConfigurationModule
import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.{Assertions, Matchers}

trait BaseRepositoryTest extends Matchers with QueryChecker with Assertions with ConfigurationModule {
  val dataSource: DataSource = {
    val source = new PGSimpleDataSource
    source.setUser(databaseUsernameConfig)
    source.setPassword(databasePasswordConfig)
    source.setUrl(databaseConnectionStringConfig)
    source
  }

  override val transactor: Transactor[IOLite] = DataSourceTransactor[IOLite](dataSource)
}