package schema.definitions

import java.util.UUID

import sangria.execution.deferred.Relation
import schema.model.Ban

object Relations {
  val banByBannedUserId = Relation[Ban, UUID]("banByBannedUserId", ban ⇒ Seq(ban.userId))
}
