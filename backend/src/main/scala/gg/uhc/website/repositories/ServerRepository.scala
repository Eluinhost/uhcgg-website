package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Server

import scala.concurrent.Future
import scalaz.Scalaz._

class ServerRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect =
    fr"SELECT id, owner, networkid, name, address, ip, port, location, region, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  def getByRelations(rel: RelationIds[Server]): Future[List[Server]] =
    (baseSelect ++ Fragments.whereAndOpt(
      rel
        .get(Relations.serverByNetworkId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"networkid".asInstanceOf[Fragment], _))
    )).query[Server].list.runOnDatabase
}
