package gg.uhc.website.server

import com.softwaremill.macwire.wire
import gg.uhc.website.routes.RoutesModule

trait ServerModule extends RoutesModule {
  lazy val server: Server = wire[Server]
}
