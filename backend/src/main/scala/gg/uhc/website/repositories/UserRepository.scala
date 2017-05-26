package gg.uhc.website.repositories

import java.util.UUID

import com.github.t3hnar.bcrypt._
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.schema.model.User

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object UserRepository {
  private val baseSelect =
    fr"SELECT id, username, email, password, created FROM users".asInstanceOf[Fragment]

  def changePasswordQuery(id: UUID, password: String): Update0 =
    sql"UPDATE users SET password = ${password.bcrypt} WHERE id = $id"
      .asInstanceOf[Fragment]
      .update

  def createUserQuery(username: String, email: String, password: String): Update0 =
    sql"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})"
      .asInstanceOf[Fragment]
      .update

  def checkUsernameInUseQuery(username: String): Query0[Long] =
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Long]

  def getByIdsQuery(ids: NonEmptyList[UUID]): Query0[User] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[User]

  def getByUsernamesQuery(usernames: NonEmptyList[String]): Query0[User] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"username".asInstanceOf[Fragment], usernames))).query[User]

  def getByIdQuery(id: UUID): Query0[User] =
    (baseSelect ++ Fragments.whereAnd(fr"id = $id".asInstanceOf[Fragment])).query[User]

  def getByUsernameQuery(username: String): Query0[User] =
    (baseSelect ++ Fragments.whereAnd(fr"username = $username".asInstanceOf[Fragment])).query[User]

  def getByUsernameOrEmail(login: String): Query0[User] =
    (baseSelect ++ Fragments.whereOr(
      fr"username = $login".asInstanceOf[Fragment],
      fr"email = $login".asInstanceOf[Fragment]
    )).query[User]
}

class UserRepository(db: DatabaseRunner) extends RepositorySupport with CustomJsonCodec {
  import db.Implicits._
  import UserRepository._
  import db.system.dispatcher

  def changePassword(id: UUID, password: String): Future[Boolean] = changePasswordQuery(id, password)
    .run
    .map(_ > 0)
    .runOnDatabase

  def createUser(username: String, email: String, password: String): Future[User] =
    createUserQuery(username, email, password)
      .withUniqueGeneratedKeys[UUID]("id")
      .runOnDatabase
      .flatMap(getById)
      .map(_.get)

  def isUsernameInUse(username: String): Future[Boolean] = checkUsernameInUseQuery(username).unique.map(_ > 0).runOnDatabase

  def getByIds(ids: Seq[UUID]): Future[List[User]] =
    ids.toList.toNel match {
      case None      ⇒ Future successful List()
      case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
    }

  def getByUsernames(usernames: Seq[String]): Future[List[User]] =
    usernames.toList.toNel match {
      case None      ⇒ Future successful List()
      case Some(nel) ⇒ getByUsernamesQuery(nel).list.runOnDatabase
    }

  def getById(id: UUID): Future[Option[User]] = getByIdQuery(id).option.runOnDatabase

  def getByUsername(username: String): Future[Option[User]] = getByUsernameQuery(username).option.runOnDatabase

  def authenticate(login: String, password: String): Future[Option[User]] =
    getByUsernameOrEmail(login)
      .list
      .map(_.find(it ⇒ password.isBcrypted(it.password)))
      .runOnDatabase
}
