package schema.definitions

import sangria.schema.{ObjectType, Schema}

class SchemaDefinition(
    userSchemaDefinition: UsersQueries,
    roleSchemaDefinition: RolesQueries,
    banSchemaDefinition: BansQueries,
    regionQueries: RegionQueries,
    versionQueries: VersionQueries,
    networkQueries: NetworkQueries,
    styleQueries: StyleQueries) {
  val schema = Schema(
    ObjectType(
      "Query",
      fields = userSchemaDefinition.query
        ::: roleSchemaDefinition.query
        ::: banSchemaDefinition.query
        ::: regionQueries.query
        ::: versionQueries.query
        ::: networkQueries.query
        ::: styleQueries.query
    )
  )
}
