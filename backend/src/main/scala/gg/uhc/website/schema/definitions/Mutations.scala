package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.scalars.UuidScalarTypeSupport
import sangria.schema._

object Mutations extends UuidScalarTypeSupport {
  val usernameArg = Argument(name = "username", argumentType = StringType, description = "Username or email to login with")
  val userIdArg = Argument(name = "id", argumentType = UuidType, description = "Username or email to login with")
  val passwordArg = Argument(name = "password", argumentType = StringType, description = "Password for the account")

  val mutations = ObjectType[SchemaContext, Unit](
    "Mutation",
    description = "Root mutation object",
    fields = fields[SchemaContext, Unit](
      Field(
        name = "login",
        description = Some("Login with the supplied username/email and password combination"),
        fieldType = OptionType(StringType),
        arguments = usernameArg :: passwordArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.login(ctx.arg(usernameArg), ctx.arg(passwordArg))
      ),
      Field(
        name = "changePassword", // TODO definitely remove this
        fieldType = BooleanType,
        arguments = userIdArg :: passwordArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.changePassword(ctx.arg(userIdArg), ctx.arg(passwordArg))
      )
    )
  )
}
