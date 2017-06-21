package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Scenario

class ScenarioRepository
    extends Repository[Scenario]
    with HasUuidIdColumn[Scenario]
    with HasRelationColumns[Scenario] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[Scenario] = implicitly[Composite[Scenario]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"name",
      p"description",
      p"created",
      p"modified",
      p"deleted",
      p"owner_user_id"
    ) from p"scenarios"

  override private[repositories] val idColumn = p"uuid"

  private[repositories] val getByOwnerUserIdQuery = relationListingQuery[UUID, Instant](
    relColumn = p"owner_user_id",
    sort = p"created".desc
  )(_)

  def getByOwnerUserId(params: RelationshipListingParameters[UUID, Instant]): ConnectionIO[List[Scenario]] =
    getByOwnerUserIdQuery(params).list
}
