import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akkahttptwirl.TwirlSupport
import routes.{ApiRoutes, HasRoutes}
import services.DatabaseService

import scala.concurrent.ExecutionContext

object Main extends App with Config with HasRoutes with TwirlSupport {
  import akka.http.scaladsl.server.Directives._

  implicit val actorSystem                     = ActorSystem()
  implicit val executor: ExecutionContext      = actorSystem.dispatcher
  implicit val log: LoggingAdapter             = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val databaseService = new DatabaseService(jdbcUrl, dbUser, dbPassword)

  // Run migrations before we do anything else
  databaseService.flyway.migrate()

  val apiRoutes = new ApiRoutes(redditConfig, databaseService)

  /**
    * Handles static assets
    */
  val resourcesRoute: Route = pathPrefix("resources") {
    getFromDirectory("build")
  }

  val renderedAppPage = html.react("app")

  override val routes =
    // check for resources first
    resourcesRoute ~
      // check for api endpoint
      apiRoutes.routes ~
      // If it doesn't match we want to send it to the frontend to handle
      complete {
        html.react("app")
      }

  Http().bindAndHandle(routes, httpHost, httpPort)
}
