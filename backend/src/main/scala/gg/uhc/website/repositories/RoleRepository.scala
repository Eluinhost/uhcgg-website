package gg.uhc.website.repositories

import doobie.imports.Fragment
import gg.uhc.website.schema.model.Role

import scalaz._
import Scalaz._
import scala.concurrent.Future
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner // Don't let IDEA auto-remove this, it's needed for the Role Array[String] permissions

class RoleRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect = fr"SELECT id, name, permissions FROM roles".asInstanceOf[Fragment]

  def getRoles: Future[List[Role]] = baseSelect.query[Role].list.runOnDatabase

  def getByIds(ids: Seq[Int]): Future[List[Role]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(
        Fragments.in(fr"id".asInstanceOf[Fragment], nel)
      )).query[Role].list.runOnDatabase
  }
}
