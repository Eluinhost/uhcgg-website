package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseService
import doobie.imports.{Fragment, _}
import gg.uhc.website.schema.model.Region

import scala.concurrent.Future
import scalaz.Scalaz._

class RegionRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT id, short, long FROM regions".asInstanceOf[Fragment]

  def getAll: Future[List[Region]] = db.run(baseSelect.query[Region].list)

  def getByIds(ids: Seq[Int]): Future[List[Region]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      db.run(
        (
          baseSelect
            ++ Fragments.whereAnd(
              Fragments.in(fr"id".asInstanceOf[Fragment], nel)
            )
        ).query[Region].list
      )
  }
}
