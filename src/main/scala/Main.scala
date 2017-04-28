import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akkahttptwirl.TwirlSupport
import routes.{ApiRoutes, HasRoutes}
import services.{DatabaseService, MigrationsService}

import scala.concurrent.ExecutionContext

object Main extends App with Config with HasRoutes with TwirlSupport {
  import akka.http.scaladsl.server.Directives._

  implicit val actorSystem                     = ActorSystem()
  implicit val executor: ExecutionContext      = actorSystem.dispatcher
  implicit val log: LoggingAdapter             = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val migrations = new MigrationsService(jdbcUrl, dbUser, dbPassword)

  migrations.migrate()

  val databaseService = new DatabaseService(jdbcUrl, dbUser, dbPassword)

  val apiRoutes = new ApiRoutes(redditConfig, databaseService)

  val resourcesRoute: Route = pathPrefix("resources") {
    getFromDirectory("build")
  }

  override val routes =
    // check for resources first
    resourcesRoute ~
      // check for api endpoint
      apiRoutes.routes ~
      // All other routes get passed to frontend to handle
      complete {
        html.react("app")
      }

  Http().bindAndHandle(routes, httpHost, httpPort)
}
