package gg.uhc.website

import gg.uhc.website.database.DatabaseModule
import gg.uhc.website.server.ServerModule

object Application extends App with ServerModule with DatabaseModule {
  import scala.concurrent.ExecutionContext.Implicits.global

  // run db migrations first thing
  migrations.migrate()

  // start the server up
  server.bind().foreach { binding ⇒
    server.afterStart(binding)

    sys.addShutdownHook {
      server.beforeStop(binding)
    }
  }
}
