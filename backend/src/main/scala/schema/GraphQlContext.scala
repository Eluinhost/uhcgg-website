package schema

import java.util.UUID

import schema.RoleSchemaDefinition.Role
import schema.UserSchemaDefinition.User

import scala.concurrent.Future

trait UserContext {
  def getByIds(ids: Seq[UUID]): Future[List[User]]
  def getById(id: UUID): Future[Option[User]]
  def getByUsername(username: String): Future[Option[User]]
  def getByUsernames(usernames: Seq[String]): Future[List[User]]
}

trait RoleContext {
  def getRoles: Future[List[Role]]
}

trait GraphQlContext {
  val users: UserContext
  val roles: RoleContext
}
