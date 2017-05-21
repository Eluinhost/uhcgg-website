package gg.uhc.website.repositories

import java.util.UUID

import com.github.t3hnar.bcrypt._
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.database.DatabaseService
import gg.uhc.website.schema.model.User

import scala.concurrent.Future
import scalaz.Scalaz._

class UserRepository(db: DatabaseService) extends RepositorySupport with CustomJsonCodec {
  def changePassword(id: UUID, password: String): Future[Boolean] = db.run(
    sql"UPDATE users SET password = ${password.bcrypt} WHERE id = $id"
      .asInstanceOf[Fragment]
      .update
      .run
      .map(_ > 0)
  )

  private[this] val baseSelect =
    fr"SELECT id, username, email, password, created FROM users".asInstanceOf[Fragment]

  import db.system.dispatcher

  def createUser(username: String, email: String, password: String): Future[User] =
    db.run(
        sql"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})"
          .asInstanceOf[Fragment]
          .update
          .withUniqueGeneratedKeys[UUID]("id")
      )
      .flatMap(getById)
      .map(_.get)

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

  def authenticate(username: String, password: String): Future[Option[User]] = db.run(
    (baseSelect
      ++ Fragments.whereOr(
        fr"username = $username".asInstanceOf[Fragment],
        fr"email = $username".asInstanceOf[Fragment]
      ))
      .asInstanceOf[Fragment]
      .query[User]
      .list
      .map(_.find(it ⇒ password.isBcrypted(it.password)))
  )
}