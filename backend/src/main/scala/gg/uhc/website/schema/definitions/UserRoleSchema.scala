package gg.uhc.website.schema.definitions

import gg.uhc.website.model.UserRole
import gg.uhc.website.schema.SchemaContext
import sangria.schema._

import scalaz.Scalaz._

object UserRoleSchema extends HasSchemaType[UserRole] {
  override lazy val Type: ObjectType[SchemaContext, UserRole] = ObjectType[SchemaContext, UserRole](
    name = "UserRole",
    description = "Connects roles ↔ users",
    fieldsFn = () ⇒
      fields[SchemaContext, UserRole](
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
