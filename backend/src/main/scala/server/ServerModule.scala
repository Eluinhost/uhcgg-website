package server

import com.softwaremill.macwire.wire
import routes.RoutesModule

trait ServerModule extends RoutesModule {
  lazy val server: Server = wire[Server]
}
