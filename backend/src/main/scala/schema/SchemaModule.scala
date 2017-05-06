package schema

import com.softwaremill.macwire.wire
import repositories.RepositoriesModule
import schema.definitions.{BansQueries, RolesQueries, SchemaDefinition, UsersQueries}

trait SchemaModule extends RepositoriesModule {
  lazy val userSchemaDefinition: UsersQueries = wire[UsersQueries]
  lazy val roleSchemaDefinition: RolesQueries = wire[RolesQueries]
  lazy val banSchemaDefinition: BansQueries   = wire[BansQueries]
  lazy val schemaDefinition: SchemaDefinition = wire[SchemaDefinition]

  lazy val schemaContext: SchemaContext = wire[SchemaContext]
}
