package schema

import com.softwaremill.macwire.wire
import schema.definitions.{RoleSchemaDefinition, SchemaDefinition, UserSchemaDefinition}

trait SchemaModule {
  lazy val userSchemaDefinition: UserSchemaDefinition = wire[UserSchemaDefinition]
  lazy val roleSchemaDefinition: RoleSchemaDefinition = wire[RoleSchemaDefinition]

  lazy val schemaDefinition: SchemaDefinition = wire[SchemaDefinition]
}
