package database.queries

import java.util.UUID

import schema.UserSchemaDefinition.User

import scalaz._

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

  def getUserById(id: UUID): ConnectionIO[Option[User]] =
    sql"SELECT id, username, email, password, created FROM users WHERE id = $id"
      .asInstanceOf[Fragment]
      .query[User]
      .option

  def getUserByUsername(name: String): ConnectionIO[Option[User]] =
    sql"SELECT id, username, email, password, created FROM users WHERE username = $name"
      .asInstanceOf[Fragment]
      .query[User]
      .option

  def getUsersByIds(ids: NonEmptyList[UUID]): ConnectionIO[List[User]] =
    (fr"""SELECT
            id,
            username,
            email,
            password,
            created
          FROM users
          WHERE """.asInstanceOf[Fragment] ++ Fragments.in(fr"id".asInstanceOf[Fragment], ids)
      ).query[User]
      .list

  def getUsersByUsernames(usernames: NonEmptyList[String]): ConnectionIO[List[User]] =
    (fr"""SELECT
            id,
            username,
            email,
            password,
            created
          FROM users
          WHERE """.asInstanceOf[Fragment] ++ Fragments.in(fr"username".asInstanceOf[Fragment], usernames)
      ).query[User]
      .list
}
