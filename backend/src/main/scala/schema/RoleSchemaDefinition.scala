package schema

import schema.context.SchemaContext

object RoleSchemaDefinition {
  import sangria.macros.derive._
  import sangria.schema._

  @GraphQLName("Role")
  @GraphQLDescription("A website role")
  case class Role(
      @GraphQLDescription("The unique ID of this role") id: Int,
      @GraphQLDescription("The unique username of this role") name: String,
      @GraphQLDescription("The granted permissions for this role") permissions: List[String])

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
