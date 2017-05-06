package repositories

import java.util.UUID

import database.DatabaseService
import doobie.imports.Fragment

import scala.concurrent.Future
import com.github.t3hnar.bcrypt._
import doobie.imports._
import doobie.postgres.imports._
import schema.model.User

import scalaz._
import Scalaz._

class UserRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect =
    fr"SELECT id, username, email, password, created FROM users".asInstanceOf[Fragment]

  def createUser(username: String, email: String, password: String): Future[UUID] = db.run(
    sql"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})"
      .asInstanceOf[Fragment]
      .update
      .withUniqueGeneratedKeys[UUID]("id")
  )

  def isUsernameInUse(username: String): Future[Boolean] = db.run(
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Int]
      .unique
      .map(_ > 0)
  )

  def getByIds(ids: Seq[UUID]): Future[List[User]] =
    ids.toList.toNel match {
      case None ⇒ Future successful List()
      case Some(nel) ⇒
        db.run(
          (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
            .query[User]
            .list
        )
    }

  def getByUsernames(usernames: Seq[String]): Future[List[User]] =
    usernames.toList.toNel match {
      case None ⇒ Future successful List()
      case Some(nel) ⇒
        db.run(
          (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"username".asInstanceOf[Fragment], nel)))
            .query[User]
            .list
        )
    }

  def getById(id: UUID): Future[Option[User]] = db.run(
    (baseSelect ++ Fragments.whereAnd(fr"id = $id".asInstanceOf[Fragment]))
      .query[User]
      .option
  )

  def getByUsername(username: String): Future[Option[User]] = db.run(
    (baseSelect ++ Fragments.whereAnd(fr"username = $username".asInstanceOf[Fragment]))
      .query[User]
      .option
  )
}
