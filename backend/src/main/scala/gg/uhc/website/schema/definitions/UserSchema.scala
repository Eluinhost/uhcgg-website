package gg.uhc.website.schema.definitions

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model._
import sangria.schema._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ArgumentConverters._
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.scalars.InstantScalarTypeSupport._
import gg.uhc.website.schema.scalars.UuidScalarTypeSupport._

import scalaz.Scalaz._

object UserSchema extends HasSchemaType[User] with HasSchemaQueries {
  private val usernameArg = Argument(name = "username", argumentType = StringType, description = "Username to match")
  private val usernamesArg = Argument(
    name = "usernames",
    argumentType = ListInputType(StringType),
    description = "List of usernames to match"
  )

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "userByUsername",
      fieldType = OptionType(Type),
      arguments = usernameArg :: Nil,
      resolve = implicit ctx ⇒ ctx.ctx.users.getByUsername(usernameArg.resolve),
      description = "Fetch a user with the given username".some
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

  override lazy val Type: ObjectType[SchemaContext, User] = ObjectType[SchemaContext, User](
    name = "User",
    description = "A website account",
    interfaces = interfaces[SchemaContext, User](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, User](
        globalIdField,
        rawIdField,
        createdField,
        modifiedField,
        // Ignore email + password fields, they should not be exposed in the API
        Field(
          name = "username",
          fieldType = StringType,
          description = "The unique username of this user".some,
          resolve = _.value.username
        ),
        // Connections below here
        relationshipField[User, Ban, UUID, Instant](
          name = "bans",
          targetType = BanSchema.Type,
          description = "All current bans applied to the given user",
          action = _.bans.getByBannedUserId,
          cursorFn = (b: Ban) ⇒ b.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, UserRole, UUID, UUID](
          name = "roles",
          targetType = UserRoleSchema.Type,
          description = "A list of user roles the user has",
          action = _.userRoles.getByUserId,
          cursorFn = (ur: UserRole) ⇒ ur.roleId,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Network, UUID, UUID](
          name = "networks",
          targetType = NetworkSchema.Type,
          description = "A list of networks the user owns",
          action = _.networks.getByOwnerUserId,
          cursorFn = (n: Network) ⇒ n.uuid,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Match, UUID, Instant](
          name = "matches",
          targetType = MatchSchema.Type,
          description = "A list of games the user is/has hosted",
          action = _.matches.getByHostUserId,
          cursorFn = (m: Match) ⇒ m.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Scenario, UUID, Instant](
          name = "scenarios",
          targetType = ScenarioSchema.Type,
          description = "A list of owned scenarios",
          action = _.scenarios.getByOwnerUserId,
          cursorFn = (s: Scenario) ⇒ s.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, NetworkPermission, UUID, UUID](
          name = "networkPermissions",
          targetType = NetworkPermissionSchema.Type,
          description = "A list of all networks with permissions",
          action = _.networkPermissions.getByUserId,
          cursorFn = (np: NetworkPermission) ⇒ np.networkId,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Server, UUID, UUID](
          name = "servers",
          targetType = ServerSchema.Type,
          description = "A list of all owned servers",
          action = _.servers.getByOwnerUserId,
          cursorFn = (s: Server) ⇒ s.uuid,
          idFn = (u: User) ⇒ u.uuid
        )
    )
  )
}
