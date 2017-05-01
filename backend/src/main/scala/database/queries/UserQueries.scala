package database.queries

import java.util.UUID

trait UserQueries {
  import com.github.t3hnar.bcrypt._
  import doobie.imports._
  import doobie.postgres.imports._

  def isUsernameInUse(username: String): ConnectionIO[Boolean] =
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Int]
      .unique
      .map(_ > 0)

  def createUser(username: String, email: String, password: String): ConnectionIO[UUID] =
    sql"INSERT INTO users (username, email, password) VALUES ($username, $email, ${password.bcrypt})"
      .asInstanceOf[Fragment]
      .update
      .withUniqueGeneratedKeys("id")
}
