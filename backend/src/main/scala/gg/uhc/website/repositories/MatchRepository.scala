package gg.uhc.website.repositories

import gg.uhc.website.model.Match

class MatchRepository extends Repository[Match] with CanQueryByIds[Match] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Match] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, hostUserId, serverId, versionId, styleId, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  private[this] val genericConnectionQuery = (column: String) ⇒
    generateConnectionQuery(
      relColumn = column,
      sortColumn = "created",
      sortColumnType = "timestamptz",
      sortDirection = DESC
  )

  private[repositories] val getByServerIdQuery   = genericConnectionQuery("serverId")
  private[repositories] val getByHostUserIdQuery = genericConnectionQuery("hostUserId")
  private[repositories] val getByStyleIdQuery    = genericConnectionQuery("styleId")
  private[repositories] val getByVersionIdQuery  = genericConnectionQuery("versionId")

  val getByServerId: ListConnection   = genericConnectionList(getByServerIdQuery)
  val getByHostUserId: ListConnection = genericConnectionList(getByHostUserIdQuery)
  val getByStyleId: ListConnection    = genericConnectionList(getByStyleIdQuery)
  val getByVersionId: ListConnection  = genericConnectionList(getByVersionIdQuery)
}
