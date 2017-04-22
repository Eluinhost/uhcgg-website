package services

class UserService {
  import doobie.imports._

  def isUsernameInUse(username: String): ConnectionIO[Boolean] =
    sql"SELECT COUNT(*) AS COUNT FROM users WHERE username = $username"
      .asInstanceOf[Fragment]
      .query[Int]
      .unique
      .map(_ > 0)
}
