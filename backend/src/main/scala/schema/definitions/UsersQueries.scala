package schema.definitions

import sangria.schema._
import schema.SchemaContext
import schema.scalars.CustomScalars._

class UsersQueries {
  val idArg       = Argument(name = "id", argumentType = UuidType, description = "ID to match")
  val usernameArg = Argument(name = "username", argumentType = StringType, description = "Username to match")

  val idsArg = Argument(name = "ids", argumentType = ListInputType(UuidType), description = "List of IDs to match")
  val usernamesArg = Argument(
    name = "usernames",
    argumentType = ListInputType(StringType),
    description = "List of usernames to match"
  )

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "userById",
      fieldType = OptionType(Types.UserType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.users.defer(ctx arg idArg),
      description = Some("Fetch a user with the given ID")
    ),
    Field(
      name = "userByUsername",
      fieldType = OptionType(Types.UserType),
      arguments = usernameArg :: Nil,
      resolve = ctx ⇒ ctx.ctx.users.getByUsername(ctx arg usernameArg),
      description = Some("Fetch a user with the given username")
    ),
    Field(
      name = "usersByIds",
      fieldType = ListType(Types.UserType),
      arguments = idsArg :: Nil,
      resolve = ctx ⇒ Fetchers.users.deferSeq(ctx arg idsArg),
      description = Some("Fetch users with the given IDs")
    ),
    Field(
      name = "usersByUsernames",
      fieldType = ListType(Types.UserType),
      arguments = usernamesArg :: Nil,
      resolve = ctx ⇒ ctx.ctx.users.getByUsernames(ctx arg usernamesArg),
      description = Some("Fetch users with the given usernames")
    )
  )
}
