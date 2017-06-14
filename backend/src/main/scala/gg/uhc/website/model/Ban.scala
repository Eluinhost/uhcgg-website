package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

case class Ban(
    uuid: UUID,
    reason: String,
    created: Instant,
    modified: Instant,
    expires: Instant,
    bannedUserId: UUID,
    authorUserId: UUID)
    extends BaseNode
    with ModificationTimesFields
