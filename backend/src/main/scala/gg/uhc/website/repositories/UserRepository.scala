package gg.uhc.website.repositories

import java.util.UUID

import com.github.t3hnar.bcrypt._
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.model.User

import scalaz.NonEmptyList
import scalaz.Scalaz._

class UserRepository extends Repository[User] with HasUuidIdColumn[User] with CustomJsonCodec {
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[User] = implicitly[Composite[User]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"username",
      p"email",
      p"password",
      p"created",
      p"modified"
    ) from p"users"

  override private[repositories] val idColumn = p"uuid"

  private[repositories] def changePasswordQuery(id: UUID, password: String): Update0 =
    (update(p"users") set (p"password" ==> password.bcrypt) where (p"uuid" === id)).build.update

  private[repositories] def createUserQuery(username: String, email: String, password: String): Update0 =
    (insertInto(p"users") values (
      p"username" ==> username,
      p"email" ==> email,
      p"password" ==> password.bcrypt
    )).build.update

  private[repositories] def checkUsernameInUseQuery(username: String): Query0[Long] =
    (select(func"COUNT" (p"username") as "COUNT") from p"users" where (p"username" === username)).build.query[Long]

  private[repositories] def getByUsernamesQuery(usernames: NonEmptyList[String]): Query0[User] =
    (baseSelect where (p"username" in usernames)).build.query[User]

  private[repositories] def getByUsernameQuery(username: String): Query0[User] =
    (baseSelect where (p"username" === username)).build.query[User]

  private[repositories] def getByUsernameOrEmailQuery(login: String): Query0[User] =
    (baseSelect where ((p"username" === login) or (p"email" === login))).build.query[User]

  def changePassword(id: UUID, password: String): ConnectionIO[Boolean] =
    changePasswordQuery(id, password).run
      .map(_ > 0)

  def createUser(username: String, email: String, password: String): ConnectionIO[User] =
    createUserQuery(username, email, password)
      .withUniqueGeneratedKeys[User]("uuid", "username", "email", "password", "created", "modified")

  def isUsernameInUse(username: String): ConnectionIO[Boolean] =
    checkUsernameInUseQuery(username).unique.map(_ > 0)

  def getByUsernames(usernames: Seq[String]): ConnectionIO[List[User]] =
    usernames match {
      case a :: as ⇒ getByUsernamesQuery(NonEmptyList(a, as: _*)).list
      case _       ⇒ List.empty[User].η[ConnectionIO]
    }

  def getByUsername(username: String): ConnectionIO[Option[User]] =
    getByUsernameQuery(username).option

  def authenticate(login: String, password: String): ConnectionIO[Option[User]] =
    getByUsernameOrEmailQuery(login).list.map(_.find(it ⇒ password.isBcrypted(it.password)))
}
