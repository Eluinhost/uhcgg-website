package gg.uhc.website.schema.definitions

import sangria.schema._
import gg.uhc.website.schema.SchemaContext

object RegionQueries {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "regionById",
      fieldType = OptionType(Types.RegionType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.regions.deferOpt(ctx arg idArg),
      description = Some("Looks up a region with the given id")
    ),
    Field(
      name = "regionsByIds",
      fieldType = ListType(Types.RegionType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = ctx ⇒ Fetchers.regions.deferSeqOpt(ctx arg idsArg),
      description = Some("Looks up regions with the given ids")
    ),
    Field(
      "regions",
      ListType(Types.RegionType),
      arguments = Nil, // TODO pagination
      resolve = ctx ⇒ ctx.ctx.regions.getAll,
      description = Some("Fetches all regions")
    )
  )
}