package schema.context

trait SchemaContext {
  val users: UserContext
  val roles: RoleContext
}

object SchemaContext {
  def apply(u: UserContext, r: RoleContext) = new SchemaContext {
    override val roles: RoleContext = r
    override val users: UserContext = u
  }
}
