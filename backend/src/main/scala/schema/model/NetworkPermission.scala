package schema.model

import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("NetworkPermission")
@GraphQLDescription("Determine who has access to a network (owner always has all permissions")
case class NetworkPermission(
    networkId: Long,
    userId: UUID,
    @GraphQLDescription("Permission flag for 'admin' permissions") // TODO define what admin is
    isAdmin: Boolean)
