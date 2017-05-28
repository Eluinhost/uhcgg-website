package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object Network {
  implicit val hasId: HasId[Network, Long] = HasId(_.id)
}

case class Network(
    id: Long,
    name: String,
    tag: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    owner: UUID)
    extends IdentificationFields[Long]
    with ModificationTimesFields
    with DeleteableFields
