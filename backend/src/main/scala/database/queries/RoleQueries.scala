package database.queries

import schema.RoleSchemaDefinition.Role

trait RoleQueries {
  import doobie.imports._
  import doobie.postgres.imports._

  def getAllRoles: ConnectionIO[List[Role]] =
    sql"SELECT id, name, permissions FROM roles"
      .asInstanceOf[Fragment]
      .query[Role]
      .list
}
