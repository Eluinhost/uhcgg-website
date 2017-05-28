package gg.uhc.website.repositories

import javax.sql.DataSource

import doobie.scalatest.QueryChecker
import doobie.util.iolite.IOLite
import doobie.util.transactor.{DataSourceTransactor, Transactor}
import gg.uhc.website.configuration.ConfigurationModule
import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.{Assertions, Matchers}
import sangria.execution.deferred.{Relation, RelationIds}

trait BaseRepositoryTest extends Matchers with QueryChecker with Assertions with ConfigurationModule {
  import scala.language.implicitConversions

  val dataSource: DataSource = {
    val source = new PGSimpleDataSource
    source.setUser(databaseUsernameConfig)
    source.setPassword(databasePasswordConfig)
    source.setUrl(databaseConnectionStringConfig)
    source
  }

  override val transactor: Transactor[IOLite] = DataSourceTransactor[IOLite](dataSource)

  protected implicit def buildRelationIds[A](rel:(Relation[A, _, _], Seq[_])): RelationIds[A] =
    buildRelationIds(Seq(rel))

  protected implicit def buildRelationIds[A](rels: Seq[(Relation[A, _, _], Seq[_])]): RelationIds[A] =
    RelationIds(rels.toMap)
}