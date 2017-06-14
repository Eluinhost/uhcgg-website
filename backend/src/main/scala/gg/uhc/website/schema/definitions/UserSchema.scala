package gg.uhc.website.schema.definitions

import gg.uhc.website.model.User
import sangria.schema._
import gg.uhc.website.schema.SchemaContext

import scalaz.Scalaz._

object UserSchema extends SchemaDefinition[User] with SchemaQueries with SchemaSupport {
  private val idArg       = Argument(name = "id", argumentType = StringType, description = "ID to match")
  private val usernameArg = Argument(name = "username", argumentType = StringType, description = "Username to match")

  private val idsArg =
    Argument(name = "ids", argumentType = ListInputType(StringType), description = "List of IDs to match")
  private val usernamesArg = Argument(
    name = "usernames",
    argumentType = ListInputType(StringType),
    description = "List of usernames to match"
  )

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "userById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.users.deferOpt(idArg.resolve),
      description = "Fetch a user with the given ID".some
    ),
    Field(
      name = "userByUsername",
      fieldType = OptionType(Type),
      arguments = usernameArg :: Nil,
      resolve = implicit ctx ⇒ ctx.ctx.users.getByUsername(usernameArg.resolve),
      description = "Fetch a user with the given username".some
    ),
    Field(
      name = "usersByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.users.deferSeqOpt(idsArg.resolve),
      description = "Fetch users with the given IDs".some
    ),
    Field(
      name = "usersByUsernames",
      fieldType = ListType(Type),
      arguments = usernamesArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(usernamesArg).length * childScore)),
      resolve = implicit ctx ⇒ ctx.ctx.users.getByUsernames(usernamesArg.resolve),
      description = "Fetch users with the given usernames".some
    )
  )

  override lazy val Type: ObjectType[Unit, User] = ObjectType(
    name = "User",
    description = "A website account",
    interfaces = interfaces[Unit, User](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      idFields[User] ++ modificationTimesFields ++ fields[Unit, User](
        // Ignore email + password fields, they should not be exposed in the API
        Field(
          name = "username",
          fieldType = StringType,
          description = "The unique username of this user".some,
          resolve = _.value.username
        ),
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "bans", // TODO pagination
          fieldType = ListType(BanSchema.Type),
          description = "All current bans applied to the given user".some,
          resolve = ctx ⇒ Fetchers.bans.deferRelSeq(Relations.banByBannedUserId, ctx.value.id)
        ),
        Field(
          name = "roles", // TODO pagination
          fieldType = ListType(UserRoleSchema.Type),
          description = "A list of user roles the user has".some,
          resolve = ctx ⇒ Fetchers.userRoles.deferRelSeq(Relations.userRoleByUserId, ctx.value.id)
        ),
        Field(
          name = "networks", // TODO pagination
          fieldType = ListType(NetworkSchema.Type),
          description = "A list of networks the user owns".some,
          resolve = ctx ⇒ Fetchers.networks.deferRelSeq(Relations.networkByUserId, ctx.value.id)
        ),
        Field(
          name = "matches",
          fieldType = ListType(MatchSchema.Type), // TODO pagination + a way to filter out old ones
          description = "A list of games the user is/has hosted".some,
          resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByHostId, ctx.value.id)
        ),
        Field(
          name = "scenarios",
          fieldType = ListType(ScenarioSchema.Type),
          description = "A list of owned scenarios".some, // TODO pagination
          resolve = ctx ⇒ Fetchers.scenarios.deferRelSeq(Relations.scenarioByOwnerId, ctx.value.id)
        ),
        Field(
          name = "networkPermissions", // TODO pagination
          fieldType = ListType(NetworkPermissionSchema.Type),
          description = "A list of all networks with permissions".some,
          resolve = ctx ⇒ Fetchers.networkPermissions.deferRelSeq(Relations.networkPermissionByUserId, ctx.value.id)
        )
    )
  )
}
