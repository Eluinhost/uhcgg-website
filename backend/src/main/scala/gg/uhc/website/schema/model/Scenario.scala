package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object Scenario {
  implicit val hasId: HasId[Scenario, Long] = HasId(_.id)
}

case class Scenario(
    id: Long,
    name: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    owner: UUID)
    extends IdentificationFields[Long]
    with ModificationTimesFields
    with DeleteableFields
