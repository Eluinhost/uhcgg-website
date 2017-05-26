package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Server

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object ServerRepository {
  private[this] val baseSelect =
    fr"SELECT id, owner, networkid, name, address, ip, port, location, region, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  def relationsQuery(networkIds: Option[NonEmptyList[Long]]): Query0[Server] =
    (baseSelect ++ Fragments.whereOrOpt(
      networkIds.map(ids ⇒ Fragments.in(fr"networkid".asInstanceOf[Fragment], ids))
    )).query[Server]
}

class ServerRepository(db: DatabaseRunner) extends RepositorySupport {
  import ServerRepository._
  import db.Implicits._

  def getByRelations(rel: RelationIds[Server]): Future[List[Server]] =
    relationsQuery(
      networkIds = rel.get(Relations.serverByNetworkId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
