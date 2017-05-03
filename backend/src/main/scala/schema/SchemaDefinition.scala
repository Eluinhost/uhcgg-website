package schema

import sangria.execution.deferred.Fetcher
import schema.context.SchemaContext

object SchemaDefinition {
  import sangria.schema._

  val schema = Schema(
    ObjectType(
      "Query",
      fields = UserSchemaDefinition.query ::: RoleSchemaDefinition.query
    )
  )

  val fetchers: List[Fetcher[SchemaContext, _, _, _]] = UserSchemaDefinition.fetcher :: Nil
}
