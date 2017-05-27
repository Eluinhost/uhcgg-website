package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object RolesQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "roleById",
      fieldType = OptionType(Types.RoleType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.roles.deferOpt(idArg.resolve),
      description = Some("Looks up a role with the given id")
    ),
    Field(
      name = "rolesByIds",
      fieldType = ListType(Types.RoleType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.roles.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up roles with the given ids")
    ),
    Field(
      "roles",
      ListType(Types.RoleType), // TODO pagination
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.roles.getAll,
      description = Some("Fetches all roles")
    )
  )
}
