package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

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
