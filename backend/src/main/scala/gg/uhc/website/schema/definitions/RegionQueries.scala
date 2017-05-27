package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object RegionQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "regionById",
      fieldType = OptionType(Types.RegionType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.regions.deferOpt(idArg.resolve),
      description = Some("Looks up a region with the given id")
    ),
    Field(
      name = "regionsByIds",
      fieldType = ListType(Types.RegionType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.regions.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up regions with the given ids")
    ),
    Field(
      "regions",
      ListType(Types.RegionType),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.regions.getAll,
      description = Some("Fetches all regions")
    )
  )
}
