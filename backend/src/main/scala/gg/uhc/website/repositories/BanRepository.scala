package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Ban

import scala.concurrent.Future
import scalaz._
import Scalaz._
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds

class BanRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseQuery: Fragment =
    fr"SELECT id, reason, created, expires, userid, author FROM bans".asInstanceOf[Fragment]

  def getBansByIds(ids: Seq[Long]): Future[List[Ban]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseQuery ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
        .query[Ban]
        .list
        .runOnDatabase
  }

  def getBans(showExpired: Boolean): Future[List[Ban]] =
    (baseQuery ++ Fragments.whereAndOpt(showExpired.option(fr"expires > NOW()".asInstanceOf[Fragment])))
      .query[Ban]
      .list
      .runOnDatabase

  def getByRelations(rel: RelationIds[Ban]): Future[List[Ban]] =
    (baseQuery ++ Fragments.whereAndOpt(
      rel
        .get(Relations.banByBannedUserId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"userid".asInstanceOf[Fragment], _))
    )).query[Ban].list.runOnDatabase
}
