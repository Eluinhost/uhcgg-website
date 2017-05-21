package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.NetworkPermission

import scala.concurrent.Future
import scalaz.Scalaz._

class NetworkPermissionRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT networkid, userid, isadmin FROM network_permissions".asInstanceOf[Fragment]

  def getByRelations(rel: RelationIds[NetworkPermission]): Future[List[NetworkPermission]] =
    db.run(
      (baseSelect ++ Fragments.whereOrOpt(
        rel
          .get(Relations.networkPermissionByUserId)
          .flatMap(_.toList.toNel) // convert to a non-empty list first
          .map(Fragments.in(fr"userid".asInstanceOf[Fragment], _)),
        rel
          .get(Relations.networkPermissionByNetworkId)
          .flatMap(_.toList.toNel)
          .map(Fragments.in(fr"networkid".asInstanceOf[Fragment], _))
      )).query[NetworkPermission].list
    )
}
