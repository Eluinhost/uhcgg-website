package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Scenario

class ScenarioRepository extends Repository[Scenario] with CanQueryByIds[Scenario] with CanQueryRelations[Scenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Scenario] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, description, created, modified, deleted, ownerUserId FROM scenarios"
      .asInstanceOf[Fragment]

  private[repositories] val getByOwnerUserIdQuery = connectionQuery[UUID, Instant](
    relColumn = "ownerUserId",
    cursorColumn = "created",
    cursorDirection = DESC
  )

  val getByOwnerUserId: LookupA[UUID, Instant] = getByOwnerUserIdQuery
}
