package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Style

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object StyleRepository {
  private[this] val baseSelect =
    fr"SELECT id, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]

  val getAllQuery: Query0[Style] = baseSelect.query[Style]

  def getByIdsQuery(ids: NonEmptyList[Int]): Query0[Style] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Style]
}

class StyleRepository(db: DatabaseRunner) extends RepositorySupport {
  import StyleRepository._
  import db.Implicits._

  def getAll: Future[List[Style]] = getAllQuery.list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Style]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }
}
