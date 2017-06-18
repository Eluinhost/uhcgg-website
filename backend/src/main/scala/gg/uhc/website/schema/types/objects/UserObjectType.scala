package gg.uhc.website.schema.types.objects

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.InstantScalarType._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import sangria.schema._

import scalaz.Scalaz._

object UserObjectType extends HasObjectType[User] {
  override lazy val Type: ObjectType[SchemaContext, User] = ObjectType[SchemaContext, User](
    name = "User",
    description = "A website account",
    interfaces = interfaces[SchemaContext, User](RelayDefinitions.nodeInterface),
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
          targetType = BanObjectType.Type,
          description = "All current bans applied to the given user",
          action = _.bans.getByBannedUserId,
          cursorFn = (b: Ban) ⇒ b.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, UserRole, UUID, UUID](
          name = "roles",
          targetType = UserRoleObjectType.Type,
          description = "A list of user roles the user has",
          action = _.userRoles.getByUserId,
          cursorFn = (ur: UserRole) ⇒ ur.roleId,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Network, UUID, UUID](
          name = "networks",
          targetType = NetworkObjectType.Type,
          description = "A list of networks the user owns",
          action = _.networks.getByOwnerUserId,
          cursorFn = (n: Network) ⇒ n.uuid,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Match, UUID, Instant](
          name = "matches",
          targetType = MatchObjectType.Type,
          description = "A list of games the user is/has hosted",
          action = _.matches.getByHostUserId,
          cursorFn = (m: Match) ⇒ m.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Scenario, UUID, Instant](
          name = "scenarios",
          targetType = ScenarioObjectType.Type,
          description = "A list of owned scenarios",
          action = _.scenarios.getByOwnerUserId,
          cursorFn = (s: Scenario) ⇒ s.created,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, NetworkPermission, UUID, UUID](
          name = "networkPermissions",
          targetType = NetworkPermissionObjectType.Type,
          description = "A list of all networks with permissions",
          action = _.networkPermissions.getByUserId,
          cursorFn = (np: NetworkPermission) ⇒ np.networkId,
          idFn = (u: User) ⇒ u.uuid
        ),
        relationshipField[User, Server, UUID, UUID](
          name = "servers",
          targetType = ServerObjectType.Type,
          description = "A list of all owned servers",
          action = _.servers.getByOwnerUserId,
          cursorFn = (s: Server) ⇒ s.uuid,
          idFn = (u: User) ⇒ u.uuid
        )
    )
  )
}
