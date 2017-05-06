package schema.definitions

import sangria.schema.{ObjectType, Schema}

class SchemaDefinition(
    userSchemaDefinition: UsersQueries,
    roleSchemaDefinition: RolesQueries,
    banSchemaDefinition: BansQueries,
    regionQueries: RegionQueries) {
  val schema = Schema(
    ObjectType(
      "Query",
      fields = userSchemaDefinition.query
        ::: roleSchemaDefinition.query
        ::: banSchemaDefinition.query
        ::: regionQueries.query
    )
  )
}
