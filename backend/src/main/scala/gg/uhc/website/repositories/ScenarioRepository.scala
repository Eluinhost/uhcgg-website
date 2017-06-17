package gg.uhc.website.repositories

import gg.uhc.website.model.Scenario

class ScenarioRepository extends Repository[Scenario] with CanQueryByIds[Scenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Scenario] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, description, created, modified, deleted, ownerUserId FROM scenarios"
      .asInstanceOf[Fragment]

  private[repositories] val getByOwnerUserIdQuery = generateConnectionQuery(
    relColumn = "ownerUserId",
    sortColumn = "created",
    sortColumnType = "timestamptz",
    sortDirection = DESC
  )

  val getByOwnerUserId: ListConnection = genericConnectionList(getByOwnerUserIdQuery)
}
