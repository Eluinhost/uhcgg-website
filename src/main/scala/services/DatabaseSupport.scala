package services

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import configuration.Config
import doobie.hikari.hikaritransactor.HikariTransactor
import doobie.imports._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.Future
import scalaz.effect.IO

trait DatabaseSupport {
  private[this] val config = new HikariConfig()
  config.setJdbcUrl(Config.jdbcUrl)
  config.setUsername(Config.dbUser)
  config.setPassword(Config.dbPassword)

  val dataSource: HikariDataSource = new HikariDataSource(config)
  val xa: Transactor[IO]       = HikariTransactor[IO](dataSource)

  def runQuery[A](query: ConnectionIO[A]): Future[A] = Future {
    query.transact(xa).unsafePerformIO()
  }(Implicits.global)
}
