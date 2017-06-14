package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

case class Scenario(
    uuid: UUID,
    name: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    ownerUserId: UUID)
    extends BaseNode
    with ModificationTimesFields
    with DeleteableFields
