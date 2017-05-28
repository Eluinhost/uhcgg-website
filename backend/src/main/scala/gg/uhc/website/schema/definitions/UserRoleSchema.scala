package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.model.UserRole
import sangria.schema._

import scalaz.Scalaz._

object UserRoleSchema extends SchemaDefinition[UserRole] with SchemaSupport {
  override lazy val Type: ObjectType[Unit, UserRole] = ObjectType(
    name = "UserRole",
    description = "Connects roles ↔ users",
    fieldsFn = () ⇒
      fields[Unit, UserRole](
        Field(
          name = "user",
          fieldType = UserSchema.Type,
          description = "The user that this applies to".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
        ),
        Field(
          name = "role",
          fieldType = RoleSchema.Type,
          description = "The role that this applies to".some,
          resolve = ctx ⇒ Fetchers.roles.defer(ctx.value.roleId)
        )
    )
  )
}
