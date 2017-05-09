package schema.model

import java.time.Instant
import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Network")
@GraphQLDescription("A collection of servers")
case class Network(
    @GraphQLDescription("The unique ID of this network") id: Long,
    @GraphQLDescription("The unique name of this network") name: String,
    @GraphQLDescription("The unique 'tag' of this network") tag: String,
    @GraphQLDescription("The markdown formatted description of the network") description: String,
    @GraphQLDescription("When the network was first created") created: Instant,
    @GraphQLDescription("When the network was last modified") modified: Instant,
    @GraphQLDescription("Whether this network was deleted or not") deleted: Boolean,
    owner: UUID)