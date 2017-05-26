package gg.uhc.website.repositories

import doobie.imports.Fragment
import gg.uhc.website.schema.model.Role

import scalaz._
import Scalaz._
import scala.concurrent.Future
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner // Don't let IDEA auto-remove this, it's needed for the Role Array[String] permissions

object RoleRepository {
  private[this] val baseSelect = fr"SELECT id, name, permissions FROM roles".asInstanceOf[Fragment]

  val getAllQuery: Query0[Role] = baseSelect.query[Role]

  def getByIdsQuery(ids: NonEmptyList[Int]): Query0[Role] =
    (baseSelect ++ Fragments.whereAnd(
      Fragments.in(fr"id".asInstanceOf[Fragment], ids)
    )).query[Role]

}

class RoleRepository(db: DatabaseRunner) extends RepositorySupport {
  import RoleRepository._
  import db.Implicits._

  def getRoles: Future[List[Role]] = getAllQuery.list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Role]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }
}
