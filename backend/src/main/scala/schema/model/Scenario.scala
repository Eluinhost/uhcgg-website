package schema.model

import java.time.Instant
import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Scenario")
@GraphQLDescription("Information about a specific scenario")
case class Scenario(
    @GraphQLDescription("The unique ID for this scenario") id: Long,
    @GraphQLDescription("The title of this scenario") name: String,
    @GraphQLDescription("The full markdown description of the scenario") description: String,
    @GraphQLDescription("When this was first created") created: Instant,
    @GraphQLDescription("When this was last modified") modified: Instant,
    @GraphQLDescription("Whether this has been deleted or not") deleted: Boolean,
    owner: UUID)
