package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object NetworkQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "networkById",
      fieldType = OptionType(Types.NetworkType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.networks.deferOpt(idArg.resolve),
      description = Some("Looks up a version with the given id")
    ),
    Field(
      name = "networksByIds",
      fieldType = ListType(Types.NetworkType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.networks.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up versions with the given ids")
    ),
    Field(
      "networks",
      ListType(Types.NetworkType),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.networks.getAll,
      description = Some("Fetches all versions")
    )
  )
}
