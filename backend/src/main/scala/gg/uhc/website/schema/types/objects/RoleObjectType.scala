package gg.uhc.website.schema.types.objects

import java.util.UUID

import gg.uhc.website.model.{Role, UserRole}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import sangria.schema._

import scalaz.Scalaz._

object RoleObjectType extends HasObjectType[Role] {
  override lazy val Type: ObjectType[SchemaContext, Role] = ObjectType[SchemaContext, Role](
    name = "Role",
    description = "A website role to grant permission to a user",
    interfaces = interfaces[SchemaContext, Role](RelayDefinitions.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Role](
        globalIdField,
        rawIdField,
        Field(
          name = "name",
          fieldType = StringType,
          description = "The unqiue name of this role".some,
          resolve = _.value.name
        ),
        Field(
          name = "permissions",
          fieldType = ListType(StringType),
          description = "The granted permissions for users with this role".some,
          resolve = _.value.permissions
        ),
        // Connections below here
        relationshipField[Role, UserRole, UUID, UUID](
          name = "users",
          targetType = UserRoleObjectType.Type,
          description = "List of UserRole objects to get users with this role",
          action = _.userRoles.getByUserId,
          cursorFn = (ur: UserRole) ⇒ ur.roleId,
          idFn = (r: Role) ⇒ r.uuid
        )
    )
  )
}
