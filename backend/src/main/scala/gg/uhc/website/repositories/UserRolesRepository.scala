package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.UserRole

import scala.concurrent.Future
import scalaz.Scalaz._

class UserRolesRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect = fr"SELECT userid, roleid FROM user_roles".asInstanceOf[Fragment]

  def search(userIds: Option[Seq[UUID]] = None, roleIds: Option[Seq[Int]] = None): Future[List[UserRole]] =
    (baseSelect ++ Fragments.whereOrOpt(
      userIds
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"userid".asInstanceOf[Fragment], _)),
      roleIds
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"roleid".asInstanceOf[Fragment], _))
    )).query[UserRole].list.runOnDatabase

  def getByRelations(rel: RelationIds[UserRole]): Future[List[UserRole]] =
    search(
      rel.get(Relations.userRoleByUserId),
      rel.get(Relations.userRoleByRoleId)
    )
}
