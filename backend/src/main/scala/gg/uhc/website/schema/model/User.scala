package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object User {
  implicit val hasId: HasId[User, UUID] = HasId(_.id)
}

case class User(id: UUID, username: String, email: String, password: String, created: Instant, modified: Instant)
    extends IdentificationFields[UUID]
    with ModificationTimesFields
