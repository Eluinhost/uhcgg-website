package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Ban

import scalaz.Scalaz._

class BanRepository extends Repository[Ban] with CanQueryByIds[Ban] with CanQueryRelations[Ban] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Ban] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, reason, created, modified, expires, bannedUserId, authorUserId FROM bans".asInstanceOf[Fragment]

  private[repositories] def getByExpiredStatusQuery(showExpired: Boolean): Query0[Ban] =
    (select ++ Fragments.whereAndOpt(showExpired.option(fr"expires > NOW()".asInstanceOf[Fragment])))
      .query[Ban]

  private[repositories] val getByBannedUserIdQuery = connectionQuery[UUID, Instant](
    relColumn = "bannedUserId",
    cursorColumn = "created",
    cursorDirection = DESC
  )

  def getBansByExpiredStatus(showExpired: Boolean): ConnectionIO[List[Ban]] =
    getByExpiredStatusQuery(showExpired).list

  val getByBannedUserId: LookupA[UUID, Instant] = getByBannedUserIdQuery
}
