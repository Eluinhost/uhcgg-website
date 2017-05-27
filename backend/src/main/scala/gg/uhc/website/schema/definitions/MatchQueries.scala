package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object MatchQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "matchById",
      fieldType = OptionType(Types.MatchType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.matches.deferOpt(idArg.resolve),
      description = Some("Looks up a match with the given id")
    ),
    Field(
      name = "matchesByIds",
      fieldType = ListType(Types.MatchType),
      arguments = idsArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.matches.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up matches with the given ids")
    ),
    Field(
      "matches",
      ListType(Types.MatchType),
      arguments = Nil, // TODO pagination + filters
      resolve = implicit ctx ⇒ ctx.ctx.matches.getAll,
      description = Some("Fetches all matches")
    )
  )
}
