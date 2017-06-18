package gg.uhc.website.schema.types.objects

import gg.uhc.website.model.UserRole
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema._

import scalaz.Scalaz._

object UserRoleObjectType extends HasObjectType[UserRole] {
  override lazy val Type: ObjectType[SchemaContext, UserRole] = ObjectType[SchemaContext, UserRole](
    name = "UserRole",
    description = "Connects roles ↔ users",
    fieldsFn = () ⇒
      fields[SchemaContext, UserRole](
        Field(
          name = "user",
          fieldType = UserObjectType.Type,
          description = "The user that this applies to".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
        ),
        Field(
          name = "role",
          fieldType = RoleObjectType.Type,
          description = "The role that this applies to".some,
          resolve = ctx ⇒ Fetchers.roles.defer(ctx.value.roleId)
        )
    )
  )
}
