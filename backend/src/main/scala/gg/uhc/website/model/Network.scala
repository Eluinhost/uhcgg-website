package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

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
