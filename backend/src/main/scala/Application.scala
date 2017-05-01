import database.DatabaseModule
import server.ServerModule

object Application extends App with ServerModule with DatabaseModule {
  import scala.concurrent.ExecutionContext.Implicits.global

  // run db migrations first thing
  migrationService.migrate()

  // start the server up
  server.bind().foreach { binding ⇒
    server.afterStart(binding)

    sys.addShutdownHook {
      server.beforeStop(binding)
    }
  }
}
