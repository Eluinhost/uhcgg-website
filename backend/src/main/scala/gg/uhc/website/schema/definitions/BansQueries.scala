package gg.uhc.website.schema.definitions

import sangria.schema._
import gg.uhc.website.schema.SchemaContext

object BansQueries {
  val showExpired = Argument(
    "showExpired",
    OptionInputType(BooleanType),
    description = "Set to true to show bans that have already expired",
    defaultValue = false
  )

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "bans",
      ListType(Types.BanType), // TODO pagination
      arguments = showExpired :: Nil,
      resolve = ctx ⇒ ctx.ctx.bans.getBans(ctx arg showExpired),
      description = Some("Look up current bans for the given user id")
    )
  )
}
