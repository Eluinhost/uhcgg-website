package schema.model

import java.time.Instant
import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Ban")
@GraphQLDescription("A ban on a particular user")
case class Ban(
    @GraphQLDescription("The unique ID of this ban") id: Long,
    @GraphQLDescription("The unique ID of the user that is banned") userId: UUID,
    @GraphQLDescription("The unique ID of the issuer of the ban") author: UUID,
    @GraphQLDescription("The reason the user is banned") reason: String,
    @GraphQLDescription("When the ban was created") created: Instant,
    @GraphQLDescription("When the ban no longer applies") expires: Instant)
