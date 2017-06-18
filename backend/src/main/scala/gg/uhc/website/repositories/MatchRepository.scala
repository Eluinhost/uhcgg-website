package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Match

class MatchRepository extends Repository[Match] with CanQueryByIds[Match] with CanQueryRelations[Match] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Match] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, hostUserId, serverId, versionId, styleId, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  private[this] val genericConnectionQuery = (column: String) ⇒
    connectionQuery[UUID, Instant](
      relColumn = column,
      cursorColumn = "created",
      cursorDirection = DESC
  )

  private[repositories] val getByServerIdQuery   = genericConnectionQuery("serverId")
  private[repositories] val getByHostUserIdQuery = genericConnectionQuery("hostUserId")
  private[repositories] val getByStyleIdQuery    = genericConnectionQuery("styleId")
  private[repositories] val getByVersionIdQuery  = genericConnectionQuery("versionId")

  val getByServerId: LookupA[UUID, Instant]   = getByServerIdQuery
  val getByHostUserId: LookupA[UUID, Instant] = getByHostUserIdQuery
  val getByStyleId: LookupA[UUID, Instant]    = getByStyleIdQuery
  val getByVersionId: LookupA[UUID, Instant]  = getByVersionIdQuery
}
