package gg.uhc.website.repositories

import javax.sql.DataSource

import gg.uhc.website.configuration.ConfigurationModule
import org.postgresql.ds.PGSimpleDataSource

trait HasDataSource extends ConfigurationModule {
  val dataSource: DataSource = {
    val source = new PGSimpleDataSource
    source.setUser(databaseUsernameConfig)
    source.setPassword(databasePasswordConfig)
    source.setUrl(databaseConnectionStringConfig)
    source
  }
}
