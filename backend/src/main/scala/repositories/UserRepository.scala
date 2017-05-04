package repositories

import java.util.UUID

import database.DatabaseService
import doobie.imports.{ConnectionIO, Fragment}

import scala.concurrent.Future
import com.github.t3hnar.bcrypt._
import doobie.imports._
import doobie.postgres.imports._
import repositories.UserRepository._
import schema.model.User

import scalaz._
import Scalaz._

object UserRepository {
  import IdeFixes._

  def isUsernameInUseQuery(username: String): ConnectionIO[Boolean] =
    sqlize"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .query[Int]
      .unique
      .map(_ > 0)

  def createUserReturningUuidQuery(username: String, email: String, password: String): ConnectionIO[UUID] =
    sqlize"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})".update
      .withUniqueGeneratedKeys("id")

  val baseSelect = sqlize"SELECT id, username, email, password, created FROM users WHERE 1=1"

  def getUserByIdQuery(id: UUID): ConnectionIO[Option[User]] =
    (baseSelect ++ fragment" AND id = $id")
      .query[User]
      .option

  def getUserByUsernameQuery(name: String): ConnectionIO[Option[User]] =
    (baseSelect ++ fragment" AND username = $name")
      .query[User]
      .option

  def getUsersByIdsQuery(ids: NonEmptyList[UUID]): ConnectionIO[List[User]] =
    (baseSelect ++ Fragments.in(fragment"id", ids)).query[User].list

  def getUsersByUsernamesQuery(usernames: NonEmptyList[String]): ConnectionIO[List[User]] =
    (baseSelect ++ Fragments.in(fragment"username", usernames))
      .query[User]
      .list
}

class UserRepository(db: DatabaseService) {
  def createUser(username: String, email: String, password: String): Future[UUID] =
    db.run(createUserReturningUuidQuery(username, email, password))

  def isUsernameInUse(username: String): Future[Boolean] =
    db.run(isUsernameInUseQuery(username))

  def getByIds(ids: Seq[UUID]): Future[List[User]] =
    ids.toList.toNel match {
      case Some(nel) ⇒ db.run(getUsersByIdsQuery(nel))
      case None      ⇒ Future successful List()
    }

  def getByUsernames(usernames: Seq[String]): Future[List[User]] =
    usernames.toList.toNel match {
      case Some(nel) ⇒ db.run(getUsersByUsernamesQuery(nel))
      case None      ⇒ Future successful List()
    }

  def getById(id: UUID): Future[Option[User]] =
    db.run(getUserByIdQuery(id))

  def getByUsername(username: String): Future[Option[User]] =
    db.run(getUserByUsernameQuery(username))
}
