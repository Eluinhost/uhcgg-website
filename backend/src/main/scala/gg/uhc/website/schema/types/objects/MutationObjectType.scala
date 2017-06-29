package gg.uhc.website.schema.types.objects

import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.types.scalars.UuidScalarType.UuidType
import gg.uhc.website.schema.helpers.ArgumentConverters._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._

import sangria.schema._

object MutationObjectType extends HasObjectType[Unit] {
  val usernameArg =
    Argument(name = "username", argumentType = StringType, description = "Username or email to login with")
  val userIdArg   = Argument(name = "id", argumentType = UuidType, description = "User ID of account to change")
  val passwordArg = Argument(name = "password", argumentType = StringType, description = "Password for the account")

  val registerEmailArg =
    Argument(name = "email", argumentType = StringType, description = "The email to register with")
  val registerJwtArg =
    Argument(name = "token", argumentType = StringType, description = "The provided token from stage 1 of registering")

  override val Type: ObjectType[SchemaContext, Unit] = ObjectType[SchemaContext, Unit](
    "Mutation",
    description = "Root mutation object",
    fields = fields[SchemaContext, Unit](
      Field(
        name = "token",
        description = Some("Get a token for the supplied username/email and password combination"),
        fieldType = OptionType(StringType),
        arguments = usernameArg :: passwordArg :: Nil,
        complexity = Some((_, _, _) ⇒ 100D),
        resolve = implicit ctx ⇒ ctx.ctx.token(username = usernameArg.resolve, password = passwordArg.resolve)
      ),
      Field(
        name = "changePassword", // TODO this shouldn't require the user ID and should take it from the logged in session instead
        fieldType = BooleanType, // TODO also this should require a logged in session too
        arguments = userIdArg :: passwordArg :: Nil,
        resolve = implicit ctx ⇒ ctx.ctx.changePassword(id = userIdArg.resolve, password = passwordArg.resolve)
      ),
      Field(
        name = "register",
        fieldType = UserObjectType.Type,
        arguments = registerEmailArg :: passwordArg :: registerJwtArg :: Nil,
        resolve =
          implicit ctx ⇒ ctx.ctx.register(registerEmailArg.resolve, passwordArg.resolve, registerJwtArg.resolve)
      ),
      Field(
        name = "complexity",
        fieldType = FloatType,
        description = Some("How complex the current query is"),
        complexity = Some((_, _, _) ⇒ 0),
        resolve = ctx ⇒ ctx.ctx.metadata.complexity.get // must exist if this is being resovled
      ),
      Field(
        name = "depth",
        fieldType = IntType,
        description = Some("How nested the current query is"),
        complexity = Some((_, _, _) ⇒ 0),
        resolve = ctx ⇒ ctx.ctx.metadata.depth.get // must exist if this is being resovled
      )
    )
  )
}
