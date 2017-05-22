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
        name = "token",
        description = Some("Get a token for the supplied username/email and password combination"),
        fieldType = OptionType(StringType),
        arguments = usernameArg :: passwordArg :: Nil,
        complexity = Some((_,_,_) ⇒ 100),
        resolve = ctx ⇒ ctx.ctx.token(username = ctx.arg(usernameArg), password = ctx.arg(passwordArg))
      ),
      Field(
        name = "changePassword", // TODO definitely remove this
        fieldType = BooleanType,
        arguments = userIdArg :: passwordArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.changePassword(id = ctx.arg(userIdArg), password = ctx.arg(passwordArg))
      )
    ) ++ Types.MetadataFields
  )
}
