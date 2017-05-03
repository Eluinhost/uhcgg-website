package repositories

import database.DatabaseService
import repositories.RoleRepository.getAllRoles
import schema.model.Role

import scala.concurrent.Future

object RoleRepository {
  import doobie.postgres.imports._ // Don't let IDEA auto-remove this, it's needed for the Role Array[String] permissions
  import doobie.imports._

  def getAllRoles: ConnectionIO[List[Role]] =
    sql"SELECT id, name, permissions FROM roles"
      .asInstanceOf[Fragment]
      .query[Role]
      .list
}

class RoleRepository(db: DatabaseService) {
  def getRoles: Future[List[Role]] = db.run(getAllRoles)
}
