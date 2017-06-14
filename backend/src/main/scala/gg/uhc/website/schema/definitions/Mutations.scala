package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._
import scalaz.Scalaz._

object Mutations extends SchemaSupport {
  val usernameArg =
    Argument(name = "username", argumentType = StringType, description = "Username or email to login with")
  val userIdArg   = Argument(name = "id", argumentType = StringType, description = "Username or email to login with")
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
        description = "Get a token for the supplied username/email and password combination".some,
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
        fieldType = UserSchema.Type,
        arguments = registerEmailArg :: passwordArg :: registerJwtArg :: Nil,
        resolve =
          implicit ctx ⇒ ctx.ctx.register(registerEmailArg.resolve, passwordArg.resolve, registerJwtArg.resolve)
      )
    ) ++ QueryMetadataSchema.queries
  )
}
