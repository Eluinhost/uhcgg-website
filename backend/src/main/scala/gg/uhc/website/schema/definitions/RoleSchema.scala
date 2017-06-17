package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{Role, UserRole}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._

import sangria.schema._

import scalaz.Scalaz._

object RoleSchema extends HasSchemaType[Role] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "roles",
      ListType(Type), // TODO replace with a connection for pagination purposes
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.roles.getAll,
      description = "Fetches all roles".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Role] = ObjectType[SchemaContext, Role](
    name = "Role",
    description = "A website role to grant permission to a user",
    interfaces = interfaces[SchemaContext, Role](RelaySchema.nodeInterface),
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
        simpleConnectionField[Role, UserRole](
          name = "users",
          target = UserRoleSchema.Type,
          description = "List of UserRole objects to get users with this role",
          action = _.userRoles.getByUserId,
          cursorFn = _.roleId.toString
        )
    )
  )
}
