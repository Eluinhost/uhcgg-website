package services

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.hikaritransactor.HikariTransactor
import doobie.imports._
import org.flywaydb.core.Flyway

import scala.concurrent.{ExecutionContext, Future}
import scalaz.effect.IO

class DatabaseService(jdbcUrl: String, dbUser: String, dbPassword: String) {
  private[this] val config = new HikariConfig()

  config.setJdbcUrl(jdbcUrl)
  config.setUsername(dbUser)
  config.setPassword(dbPassword)

  private[this] val dataSource: HikariDataSource = new HikariDataSource(config)
  private[this] val xa: Transactor[IO]           = HikariTransactor[IO](dataSource)

  val flyway = new Flyway()
  flyway.setDataSource(dataSource)

  def run[A](query: ConnectionIO[A])(implicit ec: ExecutionContext): Future[A] = Future {
    // TODO some kind of execution context just for database stuff
    query.transact(xa).unsafePerformIO()
  }
}
