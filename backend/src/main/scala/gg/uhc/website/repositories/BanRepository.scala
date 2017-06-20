package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Ban

import scalaz.Scalaz._

class BanRepository extends Repository[Ban] with HasUuidIdColumn[Ban] with HasRelationColumns[Ban] {
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Ban] = implicitly[Composite[Ban]]

  override private[repositories] val baseSelect = select(
    p"uuid",
    p"reason",
    p"created",
    p"modified",
    p"expires",
    p"banned_user_id",
    p"author_user_id"
  ) from p"bans"

  override private[repositories] val idColumn = p"uuid"

  private[repositories] def getByExpiredStatusQuery(showExpired: Boolean): Query0[Ban] =
    (baseSelect where showExpired.option(p"expires" > func"NOW"())).build.query[Ban]

  private[repositories] val getByBannedUserIdQuery = relationListingQuery[UUID, Instant](
    relColumn = p"banned_user_id",
    sort = p"created".desc
  )

  def getBansByExpiredStatus(showExpired: Boolean): ConnectionIO[List[Ban]] =
    getByExpiredStatusQuery(showExpired).list

  val getByBannedUserId: LookupA[UUID, Instant] = getByBannedUserIdQuery
}
