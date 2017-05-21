package gg.uhc.website.schema.definitions

import sangria.schema._
import gg.uhc.website.schema.SchemaContext

object MatchQueries {
  val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "matchById",
      fieldType = OptionType(Types.MatchType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.matches.deferOpt(ctx arg idArg),
      description = Some("Looks up a match with the given id")
    ),
    Field(
      name = "matchesByIds",
      fieldType = ListType(Types.MatchType),
      arguments = idsArg :: Nil,
      resolve = ctx ⇒ Fetchers.matches.deferSeqOpt(ctx arg idsArg),
      description = Some("Looks up matches with the given ids")
    ),
    Field(
      "matches",
      ListType(Types.MatchType),
      arguments = Nil, // TODO pagination + filters
      resolve = ctx ⇒ ctx.ctx.matches.getAll,
      description = Some("Fetches all matches")
    )
  )
}
