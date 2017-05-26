package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Version

import scala.concurrent.Future
import scalaz.Scalaz._

class VersionRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect = fr"SELECT id, name FROM versions".asInstanceOf[Fragment]

  // TODO something about 'selectable' so that they can be turned off but still work

  def getAll: Future[List[Version]] = baseSelect.query[Version].list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Version]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(
        Fragments.in(fr"id".asInstanceOf[Fragment], nel)
      )).query[Version].list.runOnDatabase
  }
}
