package routes

import com.softwaremill.macwire.wire
import database.DatabaseModule
import services.HelpersModule

trait RoutesModule extends HelpersModule with DatabaseModule {
  lazy val resourcesRoute: ResourcesRoute         = wire[ResourcesRoute]
  lazy val frontendRoute: FrontendRoute           = wire[FrontendRoute]
  lazy val apiRoute: ApiRoute                     = wire[ApiRoute]
  lazy val registerRoute: RegisterRoute           = wire[RegisterRoute]
  lazy val documentationRoute: DocumentationRoute = wire[DocumentationRoute]
  lazy val baseRoute: BaseRoute                   = wire[BaseRoute]
}
