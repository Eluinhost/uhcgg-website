package gg.uhc.website.repositories

import java.util.UUID

import com.github.t3hnar.bcrypt._
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.model.User

import scalaz.NonEmptyList
import scalaz.Scalaz._

class UserRepository extends Repository[User] with CanQuery[User] with CanQueryByIds[UUID, User] with CustomJsonCodec {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[User] = implicitly
  override val idParam: Param[UUID]       = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, username, email, password, created, modified FROM users".asInstanceOf[Fragment]

  private[repositories] def changePasswordQuery(id: UUID, password: String): Update0 =
    sql"UPDATE users SET password = ${password.bcrypt} WHERE id = $id"
      .asInstanceOf[Fragment]
      .update

  private[repositories] def createUserQuery(username: String, email: String, password: String): Update0 =
    sql"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})"
      .asInstanceOf[Fragment]
      .update

  private[repositories] def checkUsernameInUseQuery(username: String): Query0[Long] =
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Long]

  private[repositories] def getByUsernamesQuery(usernames: NonEmptyList[String]): Query0[User] =
    (baseSelectQuery ++ Fragments.whereAnd(Fragments.in(fr"username".asInstanceOf[Fragment], usernames))).query[User]

  private[repositories] def getByUsernameQuery(username: String): Query0[User] =
    (baseSelectQuery ++ Fragments.whereAnd(fr"username = $username".asInstanceOf[Fragment])).query[User]

  private[repositories] def getByUsernameOrEmailQuery(login: String): Query0[User] =
    (baseSelectQuery ++ Fragments.whereOr(
      fr"username = $login".asInstanceOf[Fragment],
      fr"email = $login".asInstanceOf[Fragment]
    )).query[User]

  def changePassword(id: UUID, password: String): ConnectionIO[Boolean] =
    changePasswordQuery(id, password).run
      .map(_ > 0)

  def createUser(username: String, email: String, password: String): ConnectionIO[User] =
    createUserQuery(username, email, password)
      .withUniqueGeneratedKeys[User]("id", "username", "email", "password", "created")

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
