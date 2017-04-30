import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import akkahttptwirl.TwirlSupport
import configuration.Config
import routes.{ApiRoutes, HasRoutes}
import services.{DatabaseSupport, MigrationSupport}

import scala.concurrent.ExecutionContext

object Main extends App with HasRoutes with TwirlSupport with DatabaseSupport with MigrationSupport with Directives {

  implicit val actorSystem                     = ActorSystem()
  implicit val executor: ExecutionContext      = actorSystem.dispatcher
  implicit val log: LoggingAdapter             = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // Run migrations before we do anything else
  migrations.migrate()

  val apiRoutes = new ApiRoutes()

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

  Http().bindAndHandle(routes, Config.httpHost, Config.httpPort)
}
