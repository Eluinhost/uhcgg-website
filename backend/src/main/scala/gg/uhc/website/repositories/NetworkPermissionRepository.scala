package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.NetworkPermission

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object NetworkPermissionRepository {
  private[this] val baseSelect = fr"SELECT networkid, userid, isadmin FROM network_permissions".asInstanceOf[Fragment]

  def relationsQuery(
      userIds: Option[NonEmptyList[UUID]],
      networkIds: Option[NonEmptyList[Long]]
    ): Query0[NetworkPermission] =
    (baseSelect ++ Fragments.whereOrOpt(
      userIds.map(ids ⇒ Fragments.in(fr"userid".asInstanceOf[Fragment], ids)),
      networkIds.map(ids ⇒ Fragments.in(fr"networkid".asInstanceOf[Fragment], ids))
    )).query[NetworkPermission]
}

class NetworkPermissionRepository(db: DatabaseRunner) extends RepositorySupport {
  import NetworkPermissionRepository._
  import db.Implicits._

  def getByRelations(rel: RelationIds[NetworkPermission]): Future[List[NetworkPermission]] =
    relationsQuery(
      userIds = rel.get(Relations.networkPermissionByUserId).flatMap(_.toList.toNel),
      networkIds = rel.get(Relations.networkPermissionByNetworkId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
