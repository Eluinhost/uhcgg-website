package repositories

import database.DatabaseService
import doobie.imports.Fragment
import schema.model.Role

import scala.concurrent.Future
import doobie.imports._
import doobie.postgres.imports._ // Don't let IDEA auto-remove this, it's needed for the Role Array[String] permissions

class RoleRepository(db: DatabaseService) extends RepositorySupport {
  def getRoles: Future[List[Role]] = db.run(
    sql"SELECT id, name, permissions FROM roles"
      .asInstanceOf[Fragment]
      .query[Role]
      .list
  )
}
