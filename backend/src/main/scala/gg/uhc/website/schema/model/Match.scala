package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object Match {
  implicit val hasId: HasId[Match, Long] = HasId(_.id)
}

case class Match(
    id: Long,
    host: UUID,
    serverId: Long,
    versionId: Int,
    styleId: Int,
    size: Option[Int],
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    starts: Instant)
    extends IdentificationFields[Long]
    with ModificationTimesFields
    with DeleteableFields
