package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseService
import doobie.imports.{Fragment, _}
import gg.uhc.website.schema.model.Style

import scala.concurrent.Future
import scalaz.Scalaz._

class StyleRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect =
    fr"SELECT id, shortname, fullname, description, requiressize FROM styles".asInstanceOf[Fragment]

  def getAll: Future[List[Style]] = db.run(baseSelect.query[Style].list)

  def getByIds(ids: Seq[Int]): Future[List[Style]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      db.run(
        (
          baseSelect
            ++ Fragments.whereAnd(
              Fragments.in(fr"id".asInstanceOf[Fragment], nel)
            )
        ).query[Style].list
      )
  }
}
