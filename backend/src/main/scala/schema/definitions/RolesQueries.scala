package schema.definitions

import sangria.schema._
import schema.SchemaContext

class RolesQueries {
  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "roles",
      ListType(Types.RoleType),
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.roles.getRoles,
      description = Some("Look up all roles")
    )
  )
}
