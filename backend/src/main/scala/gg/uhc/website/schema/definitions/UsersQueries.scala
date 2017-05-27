package gg.uhc.website.schema.definitions

import sangria.schema._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.scalars.CustomScalars._

object UsersQueries extends QuerySupport {
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
      resolve = implicit ctx ⇒ Fetchers.users.deferOpt(idArg.resolve),
      description = Some("Fetch a user with the given ID")
    ),
    Field(
      name = "userByUsername",
      fieldType = OptionType(Types.UserType),
      arguments = usernameArg :: Nil,
      resolve = implicit ctx ⇒ ctx.ctx.users.getByUsername(usernameArg.resolve),
      description = Some("Fetch a user with the given username")
    ),
    Field(
      name = "usersByIds",
      fieldType = ListType(Types.UserType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.users.deferSeqOpt(idsArg.resolve),
      description = Some("Fetch users with the given IDs")
    ),
    Field(
      name = "usersByUsernames",
      fieldType = ListType(Types.UserType),
      arguments = usernamesArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(usernamesArg).length * childScore)),
      resolve = implicit ctx ⇒ ctx.ctx.users.getByUsernames(usernamesArg.resolve),
      description = Some("Fetch users with the given usernames")
    )
  )
}
