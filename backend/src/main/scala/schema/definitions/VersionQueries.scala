package schema.definitions

import sangria.schema._
import schema.SchemaContext

class VersionQueries {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "versionById",
      fieldType = OptionType(Types.VersionType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.versions.deferOpt(ctx arg idArg),
      description = Some("Looks up a version with the given id")
    ),
    Field(
      name = "versionsByIds",
      fieldType = ListType(Types.VersionType),
      arguments = idsArg :: Nil,
      resolve = ctx ⇒ Fetchers.versions.deferSeqOpt(ctx arg idsArg),
      description = Some("Looks up versions with the given ids")
    ),
    Field(
      "versions",
      ListType(Types.VersionType),
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.versions.getAll,
      description = Some("Fetches all versions")
    )
  )
}
