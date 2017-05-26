package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Region

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object RegionRepository {
  private[this] val baseSelect = fr"SELECT id, short, long FROM regions".asInstanceOf[Fragment]

  val getAllQuery: Query0[Region] = baseSelect.query[Region]

  def getByIdsQuery(ids: NonEmptyList[Int]): Query0[Region] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Region]
}

class RegionRepository(db: DatabaseRunner) extends RepositorySupport {
  import RegionRepository._
  import db.Implicits._

  def getAll: Future[List[Region]] = getAllQuery.list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Region]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }
}
