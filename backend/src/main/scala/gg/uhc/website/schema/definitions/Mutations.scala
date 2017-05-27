package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.scalars.UuidScalarTypeSupport
import sangria.schema._

object Mutations extends UuidScalarTypeSupport with QuerySupport {
  val usernameArg =
    Argument(name = "username", argumentType = StringType, description = "Username or email to login with")
  val userIdArg   = Argument(name = "id", argumentType = UuidType, description = "Username or email to login with")
  val passwordArg = Argument(name = "password", argumentType = StringType, description = "Password for the account")

  val registerEmailArg =
    Argument(name = "email", argumentType = StringType, description = "The email to register with")
  val registerJwtArg =
    Argument(name = "token", argumentType = StringType, description = "The provided token from stage 1 of registering")

  val mutations = ObjectType[SchemaContext, Unit](
    "Mutation",
    description = "Root mutation object",
    fields = fields[SchemaContext, Unit](
      Field(
        name = "token",
        description = Some("Get a token for the supplied username/email and password combination"),
        fieldType = OptionType(StringType),
        arguments = usernameArg :: passwordArg :: Nil,
        complexity = Some((_, _, _) ⇒ 100),
        resolve = implicit ctx ⇒ ctx.ctx.token(username = usernameArg.resolve, password = passwordArg.resolve)
      ),
      Field(
        name = "changePassword", // TODO definitely remove this
        fieldType = BooleanType,
        arguments = userIdArg :: passwordArg :: Nil,
        resolve = implicit ctx ⇒ ctx.ctx.changePassword(id = userIdArg.resolve, password = passwordArg.resolve)
      ),
      Field(
        name = "register",
        fieldType = Types.UserType,
        arguments = registerEmailArg :: passwordArg :: registerJwtArg :: Nil,
        resolve =
          implicit ctx ⇒ ctx.ctx.register(registerEmailArg.resolve, passwordArg.resolve, registerJwtArg.resolve)
      )
    ) ++ Types.MetadataFields
  )
}
