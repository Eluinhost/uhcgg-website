package schema.definitions

import java.util.UUID

import sangria.macros.derive._
import sangria.schema._
import schema.SchemaContext
import schema.model._

object Types {
  import schema.scalars.CustomScalars._

  lazy val UserType: ObjectType[SchemaContext, User] = deriveObjectType[SchemaContext, User](
    AddFields(
      Field(
        name = "bans", // TODO pagination
        fieldType = ListType(BanType),
        description = Some("All current bans applied to the given user"),
        resolve = ctx ⇒ Fetchers.bans.deferRelSeq[UUID](Relations.banByBannedUserId, ctx.value.id)
      ),
      Field(
        name = "roles", // TODO pagination
        fieldType = ListType(UserRoleType),
        description = Some("A list of user roles the user has"),
        resolve = ctx ⇒ Fetchers.userRoles.deferRelSeq[UUID](Relations.userRoleByUserId, ctx.value.id)
      ),
      Field(
        name = "networks", // TODO pagination
        fieldType = ListType(NetworkType),
        description = Some("A list of networks the user owns"),
        resolve = ctx ⇒ Fetchers.networks.deferRelSeq(Relations.networkByUserId, ctx.value.id)
      ),
      Field(
        name = "matches",
        fieldType = ListType(MatchType), // TODO pagination + a way to filter out old ones
        description = Some("A list of games the user is/has hosted"),
        resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByHostId, ctx.value.id)
      ),
      Field(
        name = "scenarios",
        fieldType = ListType(ScenarioType),
        description = Some("A list of owned scenarios"), // TODO pagination
        resolve = ctx ⇒ Fetchers.scenarios.deferRelSeq(Relations.scenarioByOwnerId, ctx.value.id)
      ),
      Field(
        name = "networkPermissions", // TODO pagination
        fieldType = ListType(NetworkPermissionType),
        description = Some("A list of all networks with permissions"),
        resolve = ctx ⇒ Fetchers.networkPermissions.deferRelSeq(Relations.networkPermissionByUserId, ctx.value.id)
      )
    )
  )

  lazy val BanType: ObjectType[SchemaContext, Ban] = deriveObjectType[SchemaContext, Ban](
    ReplaceField(
      "userId",
      Field(
        name = "user",
        fieldType = UserType,
        description = Some("The user that this ban applies to"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
      )
    ),
    ReplaceField(
      "author",
      Field(
        name = "author",
        fieldType = UserType,
        description = Some("The user that created this ban"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.author)
      )
    )
  )

  lazy val RoleType: ObjectType[SchemaContext, Role] = deriveObjectType[SchemaContext, Role](
    AddFields(
      Field(
        name = "users", // TODO pagination + authentication
        fieldType = ListType(UserRoleType),
        description = Some("List of users that are in this role"),
        resolve = ctx ⇒ Fetchers.userRoles.deferRelSeq(Relations.userRoleByRoleId, ctx.value.id)
      )
    )
  )

  lazy val UserRoleType: ObjectType[SchemaContext, UserRole] = deriveObjectType[SchemaContext, UserRole](
    ReplaceField(
      "userId",
      Field(
        name = "user",
        fieldType = UserType,
        description = Some("The user that this applies to"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
      )
    ),
    ReplaceField(
      "roleId",
      Field(
        name = "role",
        fieldType = RoleType,
        description = Some("The role that this applies to"),
        resolve = ctx ⇒ Fetchers.roles.defer(ctx.value.roleId)
      )
    )
  )

  lazy val RegionType: ObjectType[SchemaContext, Region] = deriveObjectType[SchemaContext, Region]()

  lazy val VersionType: ObjectType[SchemaContext, Version] = deriveObjectType[SchemaContext, Version](
    AddFields(
      Field(
        name = "matches",
        fieldType = ListType(MatchType), // TODO pagination + a way to filter out old ones
        description = Some("A list of games using this version"),
        resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByVersionId, ctx.value.id)
      )
    )
  )

  lazy val NetworkType: ObjectType[SchemaContext, Network] = deriveObjectType[SchemaContext, Network](
    ReplaceField(
      "owner",
      Field(
        name = "owner",
        fieldType = UserType,
        description = Some("The owner of the network, has full control"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
      )
    ),
    AddFields(
      Field(
        name = "servers", // TODO pagination
        fieldType = ListType(ServerType),
        description = Some("All of the servers belonging to this network"),
        resolve = ctx ⇒ Fetchers.servers.deferRelSeq(Relations.serverByNetworkId, ctx.value.id)
      ),
      Field(
        name = "permissions", // TODO pagination
        fieldType = ListType(NetworkPermissionType),
        description = Some("A list of all users with permissions"),
        resolve = ctx ⇒ Fetchers.networkPermissions.deferRelSeq(Relations.networkPermissionByNetworkId, ctx.value.id)
      )
    )
  )

  lazy val StyleType: ObjectType[SchemaContext, Style] = deriveObjectType[SchemaContext, Style](
    AddFields(
      Field(
        name = "matches",
        fieldType = ListType(MatchType), // TODO pagination + a way to filter out old ones
        description = Some("A list of games using this style"),
        resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByStyleId, ctx.value.id)
      )
    )
  )

  lazy val MatchType: ObjectType[SchemaContext, Match] = deriveObjectType[SchemaContext, Match](
    ReplaceField(
      "host",
      Field(
        name = "host",
        fieldType = UserType,
        description = Some("The host for this match"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.host)
      )
    ),
    ReplaceField(
      "serverId",
      Field(
        name = "server",
        fieldType = ServerType,
        description = Some("The server this match is hosted on"),
        resolve = ctx ⇒ Fetchers.servers.defer(ctx.value.serverId)
      )
    ),
    ReplaceField(
      "versionId",
      Field(
        name = "version",
        fieldType = VersionType,
        description = Some("The version that is being hosted"),
        resolve = ctx ⇒ Fetchers.versions.defer(ctx.value.versionId)
      )
    ),
    ReplaceField(
      "styleId",
      Field(
        name = "style",
        fieldType = StyleType,
        description = Some("The team style being hosted"),
        resolve = ctx ⇒ Fetchers.styles.defer(ctx.value.styleId)
      )
    ),
    AddFields(
      Field(
        name = "scenarios",
        fieldType = ListType(MatchScenarioType), // TODO pagination
        description = Some("Scenarios for this match"),
        resolve = ctx ⇒ Fetchers.matchScenarios.deferRelSeq(Relations.matchScenarioByMatchId, ctx.value.id)
      )
    )
  )

  lazy val ServerType: ObjectType[SchemaContext, Server] = deriveObjectType[SchemaContext, Server](
    ReplaceField(
      "owner",
      Field(
        name = "owner",
        fieldType = UserType,
        description = Some("The owner of this server, has control over this server"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
      )
    ),
    ReplaceField(
      "networkId",
      Field(
        name = "network",
        fieldType = NetworkType,
        description = Some("The network this server belongs to"),
        resolve = ctx ⇒ Fetchers.networks.defer(ctx.value.networkId)
      )
    ),
    ReplaceField(
      "region",
      Field(
        name = "region",
        fieldType = RegionType,
        description = Some("The region the server is hosted in"),
        resolve = ctx ⇒ Fetchers.regions.defer(ctx.value.region)
      )
    ),
    AddFields(
      Field(
        name = "matches",
        fieldType = ListType(MatchType), // TODO pagination + a way to filter out old ones
        description = Some("A list of games hosted on this server"),
        resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByServerId, ctx.value.id)
      )
    )
  )

  lazy val ScenarioType: ObjectType[SchemaContext, Scenario] = deriveObjectType[SchemaContext, Scenario](
    ReplaceField(
      "owner",
      Field(
        name = "owner",
        fieldType = UserType,
        description = Some("The owner of this scenario"),
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
      )
    ),
    AddFields(
      Field(
        name = "matches",
        fieldType = ListType(MatchScenarioType),
        description = Some("Matches with this scenario"), // TODO pagination + filtering
        resolve = ctx ⇒ Fetchers.matchScenarios.deferRelSeq(Relations.matchScenarioByScenarioId, ctx.value.id)
      )
    )
  )

  lazy val MatchScenarioType: ObjectType[SchemaContext, MatchScenario] =
    deriveObjectType[SchemaContext, MatchScenario](
      ReplaceField(
        "matchId",
        Field(
          name = "match",
          fieldType = MatchType,
          description = Some("The associated match"),
          resolve = ctx ⇒ Fetchers.matches.defer(ctx.value.matchId)
        )
      ),
      ReplaceField(
        "scenarioId",
        Field(
          name = "scenario",
          fieldType = ScenarioType,
          description = Some("The associated scenario"),
          resolve = ctx ⇒ Fetchers.scenarios.defer(ctx.value.scenarioId)
        )
      )
    )

  lazy val NetworkPermissionType: ObjectType[SchemaContext, NetworkPermission] =
    deriveObjectType[SchemaContext, NetworkPermission](
      ReplaceField(
        "networkId",
        Field(
          name = "network",
          fieldType = NetworkType,
          description = Some("The network these permissions apply to"),
          resolve = ctx ⇒ Fetchers.networks.defer(ctx.value.networkId)
        )
      ),
      ReplaceField(
        "userId",
        Field(
          name = "user",
          fieldType = UserType,
          description = Some("The user these permissions apply to"),
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
        )
      )
    )

  lazy val QueryType = ObjectType(
    "Query",
    description = "Root query object",
    fields = BansQueries.query
      ::: MatchQueries.query
      ::: NetworkQueries.query
      ::: RegionQueries.query
      ::: RolesQueries.query
      ::: ScenarioQueries.query
      ::: StyleQueries.query
      ::: UsersQueries.query
      ::: VersionQueries.query
  )

  lazy val MutationType = ObjectType(
    "Mutation",
    description = "Root mutation object",
    fields = fields[SchemaContext, Unit](
      Field(
        name = "generateFakeData",
        fieldType = UserType,
        arguments = Nil,
        resolve = ctx ⇒ ctx.ctx.generateFakeUser()
      )
    )
  )

  lazy val SchemaType = Schema(QueryType, Some(MutationType), None)
}
