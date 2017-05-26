package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Ban

import scala.concurrent.Future
import scalaz._
import Scalaz._
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds

object BanRepository {
  private[this] val baseQuery: Fragment =
    fr"SELECT id, reason, created, expires, userid, author FROM bans".asInstanceOf[Fragment]

  def getBansByIdsQuery(ids: NonEmptyList[Long]): Query0[Ban] =
    (baseQuery ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Ban]

  def getAllBansQuery(showExpired: Boolean): Query0[Ban] =
    (baseQuery ++ Fragments.whereAndOpt(showExpired.option(fr"expires > NOW()".asInstanceOf[Fragment]))).query[Ban]

  def relationQuery(userIds: Option[NonEmptyList[UUID]]): Query0[Ban] =
    (baseQuery ++ Fragments.whereOrOpt(
      userIds.map(ids ⇒ Fragments.in(fr"userid".asInstanceOf[Fragment], ids))
    )).query[Ban]
}

class BanRepository(db: DatabaseRunner) extends RepositorySupport {
  import BanRepository._
  import db.Implicits._

  def getBansByIds(ids: Seq[Long]): Future[List[Ban]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getBansByIdsQuery(nel).list.runOnDatabase
  }

  def getBans(showExpired: Boolean): Future[List[Ban]] = getAllBansQuery(showExpired).list.runOnDatabase

  def getByRelations(rel: RelationIds[Ban]): Future[List[Ban]] =
    relationQuery(userIds = rel.get(Relations.banByBannedUserId).flatMap(_.toList.toNel)).list.runOnDatabase
}
