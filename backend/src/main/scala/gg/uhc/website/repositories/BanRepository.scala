package gg.uhc.website.repositories

import gg.uhc.website.model.Ban
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

import scalaz.Scalaz._

class BanRepository
    extends Repository[Ban]
    with CanQuery[Ban]
    with CanQueryByIds[Ban]
    with CanQueryAll[Ban]
    with CanQueryByRelations[Ban] {
  import doobie.imports._

  override val composite: Composite[Ban] = implicitly
  override implicit val idType: String = "bigint"

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, reason, created, modified, expires, userid, author FROM bans".asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Ban]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.banByBannedUserId, "userid", "uuid")
    )

  private[repositories] def getByExpiredStatusQuery(showExpired: Boolean): Query0[Ban] =
    (baseSelectQuery ++ Fragments.whereAndOpt(showExpired.option(fr"expires > NOW()".asInstanceOf[Fragment])))
      .query[Ban]

  def getBansByExpiredStatus(showExpired: Boolean): ConnectionIO[List[Ban]] =
    getByExpiredStatusQuery(showExpired).list
}
