package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseService
import doobie.imports.{Fragment, _}
import gg.uhc.website.schema.model.Version

import scala.concurrent.Future
import scalaz.Scalaz._

class VersionRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT id, name FROM versions".asInstanceOf[Fragment]

  // TODO something about 'selectable' so that they can be turned off but still work

  def getAll: Future[List[Version]] = db.run(baseSelect.query[Version].list)

  def getByIds(ids: Seq[Int]): Future[List[Version]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      db.run(
        (
          baseSelect
            ++ Fragments.whereAnd(
              Fragments.in(fr"id".asInstanceOf[Fragment], nel)
            )
        ).query[Version].list
      )
  }
}
