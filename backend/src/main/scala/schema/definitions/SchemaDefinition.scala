package schema.definitions

import sangria.execution.deferred.Fetcher
import sangria.schema.{ObjectType, Schema}
import schema.SchemaContext

class SchemaDefinition(userSchemaDefinition: UserSchemaDefinition, roleSchemaDefinition: RoleSchemaDefinition) {
  val schema = Schema(
    ObjectType(
      "Query",
      fields = userSchemaDefinition.query ::: roleSchemaDefinition.query
    )
  )

  val fetchers: List[Fetcher[SchemaContext, _, _, _]] = userSchemaDefinition.fetcher :: Nil
}
