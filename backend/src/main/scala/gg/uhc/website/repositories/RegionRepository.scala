package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Region

import scala.concurrent.Future
import scalaz.Scalaz._

class RegionRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect = fr"SELECT id, short, long FROM regions".asInstanceOf[Fragment]

  def getAll: Future[List[Region]] = baseSelect.query[Region].list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Region]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(
        Fragments.in(fr"id".asInstanceOf[Fragment], nel)
      )).query[Region].list.runOnDatabase
  }
}
