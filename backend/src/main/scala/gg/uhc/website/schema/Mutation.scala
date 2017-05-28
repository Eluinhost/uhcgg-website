package gg.uhc.website.schema

import java.util.UUID

import doobie.imports.ConnectionIO
import gg.uhc.website.model.{Role, User}
import gg.uhc.website.model.User

import scalaz.OptionT
import scalaz.Scalaz.some

trait Mutation { this: SchemaContext ⇒
  /**
    * Checks the username and password and if they are valid generates a JWT for use in authentication
    */
  def token(username: String, password: String): ConnectionIO[Option[String]] =
    (for {
      user ← OptionT[ConnectionIO, User] {
        users.authenticate(username, password)
      }
      roleIds ← OptionT[ConnectionIO, List[Int]] {
        userRoles
          .forUser(user.id)
          .map(_.map(_.roleId))
          .map(some)
      }
      roles ← OptionT[ConnectionIO, List[Role]] {
        roles
          .getByIds(roleIds)
          .map(some)
      }
    } yield apiSession.generateToken(user, roles)).run

  def changePassword(id: UUID, password: String): ConnectionIO[Boolean] =
    users.changePassword(id, password)

  /**
    * Register the user with the given email + password. Username should be contained in the provided valid JWT token
    */
  def register(email: String, password: String, token: String): ConnectionIO[User] = {
    // check valid token + username exists
    val maybeUsername = for {
      session  ← registrationSession.parseDataFromToken(token)
      username ← session.username
    } yield username

    if (maybeUsername.isEmpty)
      throw new IllegalStateException("Invalid token provided")

    // create the user and return the created data
    users.createUser(maybeUsername.get, email, password)
  }
}
