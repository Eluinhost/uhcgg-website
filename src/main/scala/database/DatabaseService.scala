package database

import com.softwaremill.tagging.@@
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import configuration._
import doobie.hikari.hikaritransactor.HikariTransactor
import doobie.imports._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.Future
import scalaz.effect.IO

class DatabaseService(
    connectionString: String @@ DatabaseConnectionStringConfig,
    username: String @@ DatabaseUsernameConfig,
    password: String @@ DatabasePasswordConfig) {
  private[this] val config = new HikariConfig()

  config.setJdbcUrl(connectionString)
  config.setUsername(username)
  config.setPassword(password)

  val dataSource: HikariDataSource = new HikariDataSource(config)
  val xa: Transactor[IO]           = HikariTransactor[IO](dataSource)

  def runQuery[A](query: ConnectionIO[A]): Future[A] =
    Future {
      query.transact(xa).unsafePerformIO()
    }(Implicits.global)
}
