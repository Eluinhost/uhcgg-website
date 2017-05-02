package schema

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.{Fetcher, HasId}

import scala.concurrent.Future

object UserSchemaDefinition {
  import sangria.macros.derive._
  import sangria.schema._
  import schema.CustomScalars._

  @GraphQLName("User")
  @GraphQLDescription("A website account")
  case class User(
      @GraphQLDescription("The unique ID of this user") id: UUID,
      @GraphQLDescription("The unique username of this user") username: String,
      @GraphQLExclude email: String,
      @GraphQLExclude password: String,
      @GraphQLDescription("The time the account was created") created: Instant)

  val user: ObjectType[GraphQlContext, User] = deriveObjectType[GraphQlContext, User]()

  val idArg       = Argument("id", OptionInputType(UuidType), description = "ID to match")
  val usernameArg = Argument("username", OptionInputType(StringType), description = "Usernames to match")

  val idsArg = Argument("ids", OptionInputType(ListInputType(UuidType)), description = "List of IDs to match")
  val usernamesArg =
    Argument("usernames", OptionInputType(ListInputType(StringType)), description = "List of usernames to match")

  val fetcher: Fetcher[GraphQlContext, User, User, UUID] = Fetcher.caching(
    (ctx: GraphQlContext, ids: Seq[UUID]) ⇒ ctx.users.getByIds(ids)
  )(HasId(_.id))

  val query: List[Field[GraphQlContext, Unit]] = fields(
    Field(
      "user",
      OptionType(user),
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
      ListType(user),
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
