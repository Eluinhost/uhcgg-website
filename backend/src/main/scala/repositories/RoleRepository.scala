package repositories

import database.DatabaseService
import doobie.imports.Fragment
import schema.model.Role

import scalaz._, Scalaz._
import scala.concurrent.Future
import doobie.imports._
import doobie.postgres.imports._ // Don't let IDEA auto-remove this, it's needed for the Role Array[String] permissions

class RoleRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT id, name, permissions FROM roles".asInstanceOf[Fragment]

  def getRoles: Future[List[Role]] = db.run(baseSelect.query[Role].list)

  def getByIds(ids: Seq[Int]): Future[List[Role]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒ db.run(
      (
        baseSelect
          ++ Fragments.whereAnd(
            Fragments.in(fr"id".asInstanceOf[Fragment], nel)
          )
      ).query[Role].list
    )
  }
}
