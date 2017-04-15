import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import routes.{ApiRoutes, HasRoutes, RegisterEndpoints, SwaggerDocumentationEndpoints}
import services.{DatabaseService, MigrationsService}

import scala.concurrent.ExecutionContext

object Main extends App with Config with CorsSupport with HasRoutes {
  import akka.http.scaladsl.server.Directives._

  implicit val actorSystem                     = ActorSystem()
  implicit val executor: ExecutionContext      = actorSystem.dispatcher
  implicit val log: LoggingAdapter             = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val migrations = new MigrationsService(jdbcUrl, dbUser, dbPassword)

  migrations.migrate()

  val databaseService = new DatabaseService(jdbcUrl, dbUser, dbPassword)

  val apiRoutes           = new ApiRoutes()
  val registrationRoutes  = new RegisterEndpoints(redditConfig)
  val documentationRotues = new SwaggerDocumentationEndpoints()

  override val routes = corsHandler(
    pathPrefix("api")(apiRoutes.routes) ~
      pathPrefix("register")(registrationRoutes.routes) ~
      documentationRotues.routes
  )

  Http().bindAndHandle(routes, httpHost, httpPort)
}
