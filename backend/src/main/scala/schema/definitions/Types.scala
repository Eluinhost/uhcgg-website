package schema.definitions

import java.util.UUID

import sangria.macros.derive.{AddFields, ReplaceField, deriveObjectType}
import sangria.schema._
import schema.SchemaContext
import schema.model._

object Types {
  import schema.scalars.CustomScalars._

  lazy val UserType: ObjectType[SchemaContext, User] = deriveObjectType[SchemaContext, User](
    AddFields(
      Field(
        name = "bans",
        fieldType = ListType(BanType),
        description = Some("All current bans applied to the given user"),
        resolve = ctx ⇒ Fetchers.bans.deferRelSeq[UUID](Relations.banByBannedUserId, ctx.value.id)
      ),
      Field(
        name = "roles",
        fieldType = ListType(UserRoleType),
        description = Some("A list of user roles the user has"),
        resolve = ctx ⇒ Fetchers.userRoles.deferRelSeq[UUID](Relations.userRoleByUserId, ctx.value.id)
      ),
      Field(
        name = "networks",
        fieldType = ListType(NetworkType),
        description = Some("A list of networks the user owns"),
        resolve = ctx ⇒ Fetchers.networks.deferRelSeq(Relations.networkByUserId, ctx.value.id)
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
  lazy val VersionType: ObjectType[SchemaContext, Version] = deriveObjectType[SchemaContext, Version]()

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
        name = "servers",
        fieldType = ListType(ServerType),
        description = Some("All of the servers belonging to this network"),
        resolve = ctx ⇒ Fetchers.servers.deferRelSeq(Relations.serverByNetworkId, ctx.value.id)
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
    )
  )
}
