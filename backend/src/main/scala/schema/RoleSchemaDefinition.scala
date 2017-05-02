package schema

object RoleSchemaDefinition {
  import sangria.macros.derive._
  import sangria.schema._

  @GraphQLName("Role")
  @GraphQLDescription("A website role")
  case class Role(
      @GraphQLDescription("The unique ID of this role") id: Int,
      @GraphQLDescription("The unique username of this role") name: String,
      @GraphQLDescription("The granted permissions for this role") permissions: List[String])

  val role: ObjectType[GraphQlContext, Role] = deriveObjectType[GraphQlContext, Role]()

  val query: List[Field[GraphQlContext, Unit]] = fields(
    Field(
      "roles",
      ListType(role),
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.roles.getRoles,
      description = Some("Look up all roles")
    )
  )
}
