package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object VersionQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "versionById",
      fieldType = OptionType(Types.VersionType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.versions.deferOpt(idArg.resolve),
      description = Some("Looks up a version with the given id")
    ),
    Field(
      name = "versionsByIds",
      fieldType = ListType(Types.VersionType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.versions.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up versions with the given ids")
    ),
    Field(
      "versions",
      ListType(Types.VersionType),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.versions.getAll,
      description = Some("Fetches all versions")
    )
  )
}
