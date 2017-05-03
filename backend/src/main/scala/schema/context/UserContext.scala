package schema.context

import java.util.UUID

import schema.UserSchemaDefinition.User

import scala.concurrent.Future

/**
  * Created by Graham on 2017-05-05.
  */
trait UserContext {
  def getByIds(ids: Seq[UUID]): Future[List[User]]
  def getById(id: UUID): Future[Option[User]]
  def getByUsername(username: String): Future[Option[User]]
  def getByUsernames(usernames: Seq[String]): Future[List[User]]
}
