package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

case class Network(
    uuid: UUID,
    name: String,
    tag: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    ownerUserId: UUID)
    extends BaseNode
    with ModificationTimesFields
    with DeleteableFields
