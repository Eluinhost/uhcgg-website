package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

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
