package gg.uhc.website.routes

import com.softwaremill.macwire.wire
import gg.uhc.website.repositories.RepositoriesModule
import gg.uhc.website.schema.SchemaModule
import services.HelpersModule

trait RoutesModule extends HelpersModule with RepositoriesModule with SchemaModule {
  lazy val resourcesRoute: AssetsRoute            = wire[AssetsRoute]
  lazy val frontendRoute: FrontendRoute           = wire[FrontendRoute]
  lazy val apiRoute: ApiRoute                     = wire[ApiRoute]
  lazy val registerRoute: RegisterRoute           = wire[RegisterRoute]
  lazy val documentationRoute: DocumentationRoute = wire[DocumentationRoute]
  lazy val baseRoute: BaseRoute                   = wire[BaseRoute]
  lazy val graphqlRoute: GraphqlRoute             = wire[GraphqlRoute]
}
