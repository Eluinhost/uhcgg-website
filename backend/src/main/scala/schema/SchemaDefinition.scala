package schema

import sangria.execution.deferred.Fetcher

object SchemaDefinition {
  import sangria.schema._

  val schema = Schema(
    ObjectType(
      "Query",
      fields = UserSchemaDefinition.query ::: RoleSchemaDefinition.query
    )
  )

  val fetchers: List[Fetcher[GraphQlContext, _, _, _]] = UserSchemaDefinition.fetcher :: Nil
}
