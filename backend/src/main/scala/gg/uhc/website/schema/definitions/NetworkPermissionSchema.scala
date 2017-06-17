package gg.uhc.website.schema.definitions

import gg.uhc.website.model.NetworkPermission
import gg.uhc.website.schema.SchemaContext
import sangria.schema._

import scalaz.Scalaz._

object NetworkPermissionSchema extends HasSchemaType[NetworkPermission] {
  override lazy val Type: ObjectType[SchemaContext, NetworkPermission] = ObjectType[SchemaContext, NetworkPermission](
    name = "NetworkPermission",
    description = "Determine who has access to a network (owner always has all permissions",
    fieldsFn = () ⇒
      fields[SchemaContext, NetworkPermission](
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
