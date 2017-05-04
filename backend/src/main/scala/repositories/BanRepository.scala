package repositories

import java.util.UUID

import database.DatabaseService
import repositories.BanRepository.getByUserIdQuery
import schema.model.Ban

import scala.concurrent.Future

object BanRepository {
  import IdeFixes._
  import doobie.imports._
  import doobie.postgres.imports._

  def getByUserIdQuery(id: UUID, showExpired: Boolean): ConnectionIO[List[Ban]] = {
    var query = sqlize"SELECT id, userid, author, reason, created, expires FROM bans WHERE userid = $id"

    if (!showExpired)
      query ++= fragment" AND expires > NOW()"

    query.query[Ban].list
  }
}

class BanRepository(db: DatabaseService) {
  def getBansForUser(id: UUID, showExpired: Boolean): Future[List[Ban]] = db.run(getByUserIdQuery(id, showExpired))
}
