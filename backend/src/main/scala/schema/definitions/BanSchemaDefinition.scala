package schema.definitions

import sangria.macros.derive._
import sangria.schema._
import schema.SchemaContext
import schema.model.Ban
import schema.scalars.CustomScalars.{UuidType, DateType}

class BanSchemaDefinition {
  val ban: ObjectType[SchemaContext, Ban] = deriveObjectType[SchemaContext, Ban]()

  val id = Argument("id", UuidType, description = "ID of the user to lookup bans for")
  val showExpired = Argument(
    "showExpired",
    OptionInputType(BooleanType),
    description = "Set to true to show bans that have already expired",
    defaultValue = false
  )

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "bans",
      ListType(ban),
      arguments = id :: showExpired :: Nil,
      resolve = ctx ⇒ ctx.ctx.bans.getBansForUser(ctx arg id, ctx arg showExpired),
      description = Some("Look up current bans for the given user id")
    )
  )
}
