package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{Network, NetworkPermission, Server}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._

import sangria.schema._

import scalaz.Scalaz._

object NetworkSchema extends HasSchemaType[Network] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "networks",
      ListType(Type),
      arguments = Nil, // TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.networks.getAll,
      description = "Fetches all versions".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Network] = ObjectType[SchemaContext, Network](
    name = "Network",
    description = "A collection of servers",
    interfaces = interfaces[SchemaContext, Network](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Network](
        globalIdField,
        rawIdField,
        modifiedField,
        createdField,
        deletedField,
        Field(
          name = "name",
          fieldType = StringType,
          description = "The unique name of this network".some,
          resolve = _.value.name
        ),
        Field(
          name = "tag",
          fieldType = StringType,
          description = "The unique tag of this network".some,
          resolve = _.value.tag
        ),
        Field(
          name = "description",
          fieldType = StringType,
          description = "A markdown formatted description of this network".some,
          resolve = _.value.description
        ),
        // relations below here
        Field(
          name = "owner",
          fieldType = UserSchema.Type,
          description = "The owner of the network, has full control".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.ownerUserId)
        ),
        // connections below here
        simpleConnectionField[Network, Server](
          name = "servers",
          target = ServerSchema.Type,
          description = "A list of all servers belonging to this network",
          action = _.servers.getByNetworkId,
          cursorFn = _.uuid.toString
        ),
        simpleConnectionField[Network, NetworkPermission](
          name = "permissions",
          target = NetworkPermissionSchema.Type,
          description = "A list of all users with permissions",
          action = _.networkPermissions.getByNetworkId,
          cursorFn = _.userId.toString
        )
    )
  )
}
