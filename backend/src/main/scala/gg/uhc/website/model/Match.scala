package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

case class Match(
    uuid: UUID,
    hostUserId: UUID,
    serverId: UUID,
    versionId: UUID,
    styleId: UUID,
    size: Option[Int],
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    starts: Instant)
    extends BaseNode
    with ModificationTimesFields
    with DeleteableFields
