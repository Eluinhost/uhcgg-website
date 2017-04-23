package services

import java.util.UUID

class UserService {
  import doobie.imports._
  import doobie.postgres.imports._

  def isUsernameInUse(username: String): ConnectionIO[Boolean] =
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Int]
      .unique
      .map(_ > 0)

  def createUser(username: String, email: String, password: String): ConnectionIO[UUID] =
    sql"INSERT INTO users (username, email, password) VALUES ($username, $email, $password)" // TODO hashing
      .asInstanceOf[Fragment]
      .update
      .withUniqueGeneratedKeys("id")
}
