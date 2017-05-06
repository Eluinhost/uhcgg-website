package repositories

import database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import schema.definitions.Relations
import schema.model.UserRole

import scala.concurrent.Future
import scalaz.Scalaz._

class UserRolesRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT userid, roleid FROM user_roles".asInstanceOf[Fragment]

  def getByRelations(rel: RelationIds[UserRole]): Future[List[UserRole]] =
    db.run(
      (baseSelect ++ Fragments.whereAndOpt(
        rel
          .get(Relations.userRoleByUserId)
          .flatMap(_.toList.toNel) // convert to a non-empty list first
          .map(Fragments.in(fr"userid".asInstanceOf[Fragment], _)),
        rel
          .get(Relations.userRoleByRoleId)
          .flatMap(_.toList.toNel)
          .map(Fragments.in(fr"roleid".asInstanceOf[Fragment], _))
      )).query[UserRole].list
    )
}
