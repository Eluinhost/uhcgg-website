package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Scenario {
  implicit val hasId: HasId[Scenario, Long] = HasId(_.id)
}

@GraphQLName("Scenario")
@GraphQLDescription("Information about a specific scenario")
case class Scenario(
    @GraphQLDescription("The unique ID for this scenario") id: Long,
    @GraphQLDescription("The title of this scenario") name: String,
    @GraphQLDescription("The full markdown description of the scenario") description: String,
    @GraphQLDescription("When this was first created") created: Instant,
    @GraphQLDescription("When this was last modified") modified: Instant,
    @GraphQLDescription("Whether this has been deleted or not") deleted: Boolean,
    owner: UUID) extends IdentifiableModel[Long]