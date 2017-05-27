package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Ban {
  implicit val hasId: HasId[Ban, Long] = HasId(_.id)
}

@GraphQLName("Ban")
@GraphQLDescription("A ban on a particular user")
case class Ban(
    @GraphQLDescription("The unique ID of this ban") id: Long,
    @GraphQLDescription("The reason the user is banned") reason: String,
    @GraphQLDescription("When the ban was created") created: Instant,
    @GraphQLDescription("When the ban no longer applies") expires: Instant,
    userId: UUID,
    author: UUID)
    extends IdentifiableModel[Long]
