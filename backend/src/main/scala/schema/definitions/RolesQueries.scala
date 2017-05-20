package schema.definitions

import sangria.schema._
import schema.SchemaContext

object RolesQueries {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "roleById",
      fieldType = OptionType(Types.RoleType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.roles.deferOpt(ctx arg idArg),
      description = Some("Looks up a role with the given id")
    ),
    Field(
      name = "rolesByIds",
      fieldType = ListType(Types.RoleType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = ctx ⇒ Fetchers.roles.deferSeqOpt(ctx arg idsArg),
      description = Some("Looks up roles with the given ids")
    ),
    Field(
      "roles",
      ListType(Types.RoleType), // TODO pagination
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.roles.getRoles,
      description = Some("Fetches all roles")
    )
  )
}
