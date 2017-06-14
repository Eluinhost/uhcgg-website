package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._
import scalaz.Scalaz._

object ApplicationSchema
    extends Schema[SchemaContext, Unit](
      ObjectType(
        name = "Query",
        description = "Root query object",
        fields = BanSchema.queries
          ::: MatchSchema.queries
          ::: NetworkSchema.queries
          ::: RegionSchema.queries
          ::: RoleSchema.queries
          ::: ScenarioSchema.queries
          ::: StyleSchema.queries
          ::: UserSchema.queries
          ::: VersionSchema.queries
          ::: QueryMetadataSchema.queries
          ::: RelaySchema.queries
      ),
      Mutations.mutations.some,
      none
    )
