package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Match {
  implicit val hasId: HasId[Match, Long] = HasId(_.id)
}

@GraphQLName("Match")
@GraphQLDescription("An individual match")
case class Match(
    @GraphQLDescription("The unique ID of this match") id: Long,
    host: UUID,
    serverId: Long,
    versionId: Int,
    styleId: Int,
    @GraphQLDescription("The size relating to the specific style") size: Option[Int],
    @GraphQLDescription("When this was first created") created: Instant,
    @GraphQLDescription("When this was last modified") modified: Instant,
    @GraphQLDescription("Whether this has been deleted or not") deleted: Boolean,
    @GraphQLDescription("When the match starts") starts: Instant)
    extends IdentifiableModel[Long]
