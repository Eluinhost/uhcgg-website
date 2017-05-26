package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.UserRole

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object UserRolesRepository {
  private[this] val baseSelect = fr"SELECT userid, roleid FROM user_roles".asInstanceOf[Fragment]

  def relationsQuery(userIds: Option[NonEmptyList[UUID]], roleIds: Option[NonEmptyList[Int]]): Query0[UserRole] =
    (baseSelect ++ Fragments.whereOrOpt(
      userIds.map(ids ⇒ Fragments.in(fr"userid".asInstanceOf[Fragment], ids)),
      roleIds.map(ids ⇒ Fragments.in(fr"roleid".asInstanceOf[Fragment], ids))
    )).query[UserRole]
}

class UserRolesRepository(db: DatabaseRunner) extends RepositorySupport {
  import UserRolesRepository._
  import db.Implicits._

  def search(userIds: Option[Seq[UUID]] = None, roleIds: Option[Seq[Int]] = None): Future[List[UserRole]] =
    relationsQuery(
      userIds = userIds.flatMap(_.toList.toNel),
      roleIds = roleIds.flatMap(_.toList.toNel)
    ).list.runOnDatabase

  def getByRelations(rel: RelationIds[UserRole]): Future[List[UserRole]] =
    search(
      rel.get(Relations.userRoleByUserId),
      rel.get(Relations.userRoleByRoleId)
    )
}
