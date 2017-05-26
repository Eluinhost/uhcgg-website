package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.Version

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object VersionRepository {
  private[this] val baseSelect = fr"SELECT id, name FROM versions".asInstanceOf[Fragment]

  val getAllQuery: Query0[Version] = baseSelect.query[Version]

  def getByIdsQuery(ids: NonEmptyList[Int]): Query0[Version] =
    (baseSelect ++ Fragments.whereAnd(
      Fragments.in(fr"id".asInstanceOf[Fragment], ids)
    )).query[Version]

}

class VersionRepository(db: DatabaseRunner) extends RepositorySupport {
  import VersionRepository._
  import db.Implicits._

  // TODO something about 'selectable' so that they can be turned off but still work

  def getAll: Future[List[Version]] = getAllQuery.list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Version]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }
}
