package schema

import com.softwaremill.macwire.wire
import repositories.RepositoriesModule
import schema.definitions.{BanSchemaDefinition, RoleSchemaDefinition, SchemaDefinition, UserSchemaDefinition}

trait SchemaModule extends RepositoriesModule {
  lazy val userSchemaDefinition: UserSchemaDefinition = wire[UserSchemaDefinition]
  lazy val roleSchemaDefinition: RoleSchemaDefinition = wire[RoleSchemaDefinition]
  lazy val banSchemaDefinition: BanSchemaDefinition   = wire[BanSchemaDefinition]
  lazy val schemaDefinition: SchemaDefinition         = wire[SchemaDefinition]

  lazy val schemaContext: SchemaContext = wire[SchemaContext]
}
