package schema.definitions

import java.util.UUID

import sangria.macros.derive.{AddFields, ReplaceField, deriveObjectType}
import sangria.schema.{Field, ListType, ObjectType}
import schema.SchemaContext
import schema.model.{Ban, Role, User}

object Types {
  import schema.scalars.CustomScalars._

  lazy val UserType: ObjectType[SchemaContext, User] = deriveObjectType[SchemaContext, User](
    AddFields(
      Field(
        name = "bans",
        fieldType = ListType(BanType),
        description = Some("All current bans applied to the given user"),
        resolve = ctx ⇒ Fetchers.bans.deferRelSeq[UUID](Relations.banByBannedUserId, ctx.value.id)
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

  lazy val RoleType: ObjectType[SchemaContext, Role] = deriveObjectType[SchemaContext, Role]()
}
