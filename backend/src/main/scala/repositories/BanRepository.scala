package repositories

import java.util.UUID

import database.DatabaseService
import repositories.BanRepository.getByUserIdQuery
import schema.model.Ban

import scala.concurrent.Future

object BanRepository {
  import doobie.imports._
  import doobie.postgres.imports._

  def getByUserIdQuery(id: UUID, showExpired: Boolean): ConnectionIO[List[Ban]] = {
    var query =
      sql"SELECT id, userid, author, reason, created, expires FROM bans WHERE userid = $id"
        .asInstanceOf[Fragment]

    if (!showExpired)
      query ++= fr" AND expires > NOW()".asInstanceOf[Fragment]

    query.query[Ban].list
  }
}

class BanRepository(db: DatabaseService) {
  def getBansForUser(id: UUID, showExpired: Boolean): Future[List[Ban]] = db.run(getByUserIdQuery(id, showExpired))
}
