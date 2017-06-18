package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Match

class MatchRepository extends Repository[Match] with HasUuidIdColumn[Match] with HasRelationColumns[Match] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Match] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, hostUserId, serverId, versionId, styleId, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  private[this] val genericRelationListing = (column: String) ⇒
    relationListingQuery[UUID, Instant](
      relColumn = column,
      cursorColumn = "created",
      cursorDirection = SortDirection.DESC
  )

  private[repositories] val getByServerIdQuery   = genericRelationListing("serverId")
  private[repositories] val getByHostUserIdQuery = genericRelationListing("hostUserId")
  private[repositories] val getByStyleIdQuery    = genericRelationListing("styleId")
  private[repositories] val getByVersionIdQuery  = genericRelationListing("versionId")

  val getByServerId: LookupA[UUID, Instant]   = getByServerIdQuery
  val getByHostUserId: LookupA[UUID, Instant] = getByHostUserIdQuery
  val getByStyleId: LookupA[UUID, Instant]    = getByStyleIdQuery
  val getByVersionId: LookupA[UUID, Instant]  = getByVersionIdQuery
}
