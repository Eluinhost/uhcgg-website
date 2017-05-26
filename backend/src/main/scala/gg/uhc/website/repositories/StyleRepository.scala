package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Style

import scala.concurrent.Future
import scalaz.Scalaz._

class StyleRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect =
    fr"SELECT id, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]

  def getAll: Future[List[Style]] = baseSelect.query[Style].list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Style]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(
        Fragments.in(fr"id".asInstanceOf[Fragment], nel)
      )).query[Style].list.runOnDatabase
  }
}
