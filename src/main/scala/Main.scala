import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object Main extends App with Config with CorsSupport {
  import akka.http.scaladsl.server.Directives._

  implicit val actorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val migrations = new MigrationsService(jdbcUrl, dbUser, dbPassword)

  migrations.migrate()

  val databaseService = new DatabaseService(jdbcUrl, dbUser, dbPassword)

  val apiService = new ApiService(actorSystem)
  val userCreationService = new UserCreationService(redditConfig)

  val routes = corsHandler {
    apiService.routes ~
    userCreationService.routes
  }

  Http().bindAndHandle(routes, httpHost, httpPort)
}
