package gg.uhc.website.schema.types.objects

import java.util.UUID

import gg.uhc.website.model.{Network, NetworkPermission, Server}
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema._

import scalaz.Scalaz._

object NetworkObjectType extends HasObjectType[Network] {
  override lazy val Type: ObjectType[SchemaContext, Network] = ObjectType[SchemaContext, Network](
    name = "Network",
    description = "A collection of servers",
    interfaces = interfaces[SchemaContext, Network](RelayDefinitions.nodeInterface),
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
          fieldType = UserObjectType.Type,
          description = "The owner of the network, has full control".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.ownerUserId)
        ),
        // connections below here
        relationshipField[Network, Server, UUID, UUID](
          name = "servers",
          targetType = ServerObjectType.Type,
          description = "A list of all servers belonging to this network",
          action = _.servers.getByNetworkId,
          cursorFn = (server: Server) ⇒ server.uuid,
          idFn = (network: Network) ⇒ network.uuid
        ),
        relationshipField[Network, NetworkPermission, UUID, UUID](
          name = "permissions",
          targetType = NetworkPermissionObjectType.Type,
          description = "A list of all users with permissions to this network",
          action = _.networkPermissions.getByNetworkId,
          cursorFn = (np: NetworkPermission) ⇒ np.userId,
          idFn = (n: Network) ⇒ n.uuid
        )
    )
  )
}
