package schema.definitions

import sangria.schema._
import schema.SchemaContext
import schema.scalars.CustomScalars._

import scala.concurrent.Future

class UsersQueries {
  val idArg       = Argument("id", OptionInputType(UuidType), description = "ID to match")
  val usernameArg = Argument("username", OptionInputType(StringType), description = "Usernames to match")

  val idsArg = Argument("ids", OptionInputType(ListInputType(UuidType)), description = "List of IDs to match")
  val usernamesArg =
    Argument("usernames", OptionInputType(ListInputType(StringType)), description = "List of usernames to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "user",
      OptionType(Types.UserType),
      arguments = idArg :: usernameArg :: Nil,
      resolve = ctx ⇒
        ctx.withArgs(idArg, usernameArg) {
          case (Some(_), Some(_)) | (None, None) ⇒
            Future failed new IllegalArgumentException("Must provide either an ID or a username")
          case (Some(id), _)       ⇒ ctx.ctx.users.getById(id)
          case (_, Some(username)) ⇒ ctx.ctx.users.getByUsername(username)
      },
      description =
        Some("Look up a single user either by their ID or their username. Arguments are mutually exclusive")
    ),
    Field(
      "users",
      ListType(Types.UserType),
      arguments = idsArg :: usernamesArg :: Nil,
      resolve = ctx ⇒
        ctx.withArgs(idsArg, usernamesArg) { (maybeIds, maybeUsernames) ⇒
          import scala.concurrent.ExecutionContext.Implicits.global

          val idsFuture       = maybeIds.map(ctx.ctx.users.getByIds).getOrElse(Future successful List())
          val usernamesFuture = maybeUsernames.map(ctx.ctx.users.getByUsernames).getOrElse(Future successful List())

          for {
            byId       ← idsFuture
            byUsername ← usernamesFuture
          } yield byId ::: byUsername
      },
      description = Some("Look up users based on ids and/or usernames")
    )
  )
}
