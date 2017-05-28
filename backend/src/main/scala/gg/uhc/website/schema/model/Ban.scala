package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object Ban {
  implicit val hasId: HasId[Ban, Long] = HasId(_.id)
}

case class Ban(
    id: Long,
    reason: String,
    created: Instant,
    modified: Instant,
    expires: Instant,
    userId: UUID,
    author: UUID)
    extends IdentificationFields[Long]
    with ModificationTimesFields
