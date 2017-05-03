package schema.definitions

import sangria.macros.derive._
import sangria.schema._
import schema.SchemaContext
import schema.model.Role

class RoleSchemaDefinition {

  val role: ObjectType[SchemaContext, Role] = deriveObjectType[SchemaContext, Role]()

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "roles",
      ListType(role),
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.roles.getRoles,
      description = Some("Look up all roles")
    )
  )
}
