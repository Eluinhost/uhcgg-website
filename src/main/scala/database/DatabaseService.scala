package database

import akka.actor.ActorSystem
import com.softwaremill.tagging.@@
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import configuration.{DatabaseConnectionStringConfig, DatabasePasswordConfig, DatabaseUsernameConfig}
import doobie.hikari.hikaritransactor.HikariTransactor

import scala.concurrent.{ExecutionContext, Future}

class DatabaseService(
    connectionString: String @@ DatabaseConnectionStringConfig,
    username: String @@ DatabaseUsernameConfig,
    password: String @@ DatabasePasswordConfig) {
  import doobie.imports._

  private[this] val config = new HikariConfig()

  config.setJdbcUrl(connectionString)
  config.setUsername(username)
  config.setPassword(password)

  implicit val system               = ActorSystem("database-access")
  implicit val ec: ExecutionContext = system.dispatcher

  val dataSource: HikariDataSource = new HikariDataSource(config)
  val xa: Transactor[IOLite]       = HikariTransactor[IOLite](dataSource)

  def run[A](query: ConnectionIO[A]): Future[A] =
    Future {
      query.transact(xa).unsafePerformIO
    }(system.dispatcher)
}
