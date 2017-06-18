package gg.uhc.website.repositories

import java.util.UUID
import javax.sql.DataSource

import doobie.scalatest.QueryChecker
import doobie.util.iolite.IOLite
import doobie.util.transactor.{DataSourceTransactor, Transactor}
import gg.uhc.website.configuration.ConfigurationModule
import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.{Assertions, Matchers}

import scalaz.NonEmptyList

trait BaseRepositoryTest extends Matchers with QueryChecker with Assertions with ConfigurationModule {
  def randId: UUID = UUID.randomUUID()
  def randIds: NonEmptyList[UUID] = NonEmptyList(randId, randId)
  def randIdsSeq: Seq[UUID] = randId :: randId :: Nil

  val dataSource: DataSource = {
    val source = new PGSimpleDataSource
    source.setUser(databaseUsernameConfig)
    source.setPassword(databasePasswordConfig)
    source.setUrl(databaseConnectionStringConfig)
    source
  }

  override val transactor: Transactor[IOLite] = DataSourceTransactor[IOLite](dataSource)
}
