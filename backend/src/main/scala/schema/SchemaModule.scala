package schema

import com.softwaremill.macwire.wire
import repositories.RepositoriesModule
import schema.definitions._

trait SchemaModule extends RepositoriesModule {
  lazy val userSchemaDefinition: UsersQueries = wire[UsersQueries]
  lazy val roleSchemaDefinition: RolesQueries = wire[RolesQueries]
  lazy val banSchemaDefinition: BansQueries   = wire[BansQueries]
  lazy val regionQueries: RegionQueries       = wire[RegionQueries]
  lazy val versionQueries: VersionQueries     = wire[VersionQueries]
  lazy val networkQueries: NetworkQueries     = wire[NetworkQueries]
  lazy val styleQueries: StyleQueries         = wire[StyleQueries]

  lazy val schemaDefinition: SchemaDefinition = wire[SchemaDefinition]
  lazy val schemaContext: SchemaContext       = wire[SchemaContext]
}
