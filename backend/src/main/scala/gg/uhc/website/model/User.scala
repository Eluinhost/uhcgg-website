package gg.uhc.website.model

import java.time.Instant
import java.util.UUID

case class User(id: UUID, username: String, email: String, password: String, created: Instant, modified: Instant)
    extends IdentificationFields[UUID]
    with ModificationTimesFields
