package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.model.NetworkPermission
import sangria.schema._

import scalaz.Scalaz._

object NetworkPermissionSchema extends SchemaDefinition[NetworkPermission] with SchemaSupport {
  override lazy val Type: ObjectType[Unit, NetworkPermission] = ObjectType(
    name = "NetworkPermission",
    description = "Determine who has access to a network (owner always has all permissions",
    fieldsFn = () ⇒
      fields[Unit, NetworkPermission](
        Field(
          name = "isAdmin",
          fieldType = BooleanType,
          description = "Whether the user has admin permissions or not".some,
          resolve = _.value.isAdmin
        ),
        // relations below here
        Field(
          name = "network",
          fieldType = NetworkSchema.Type,
          description = "The network these permissions apply to".some,
          resolve = ctx ⇒ Fetchers.networks.defer(ctx.value.networkId)
        ),
        Field(
          name = "user",
          fieldType = UserSchema.Type,
          description = "The user these permissions apply to".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
        )
    )
  )
}
