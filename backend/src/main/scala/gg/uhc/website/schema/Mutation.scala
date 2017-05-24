package gg.uhc.website.schema

import java.util.UUID

import gg.uhc.website.schema.model.User

import scala.concurrent.Future
import scalaz.OptionT._
import scalaz.std.scalaFuture.futureInstance

trait Mutation { this: SchemaContext ⇒
  def token(username: String, password: String): Future[Option[String]] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    (for {
      user      ← optionT(users.authenticate(username, password))
      userRoles ← optionT(userRoles.search(userIds = Some(Seq(user.id))).map(Option(_)))
      roles     ← optionT(roles.getByIds(userRoles.map(_.roleId)).map(Option(_)))
    } yield apiSession.generateToken(user, roles)).run
  }

  def changePassword(id: UUID, password: String): Future[Boolean] = users.changePassword(id, password)

  def register(email: String, password: String, token: String): Future[User] = {
    // check valid token + username exists
    val maybeUsername = for {
      session  ← registrationSession.parseDataFromToken(token)
      username ← session.username
    } yield username

    if (maybeUsername.isEmpty)
      Future failed new IllegalStateException("Invalid token provided")

    users.createUser(maybeUsername.get, email, password)
  }
}
