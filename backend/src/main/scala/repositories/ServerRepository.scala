package repositories

import database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import schema.definitions.Relations
import schema.model.Server

import scala.concurrent.Future
import scalaz.Scalaz._

class ServerRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect =
    fr"SELECT id, owner, networkid, name, address, ip, port, location, region, created, modified, deleted FROM servers"
      .asInstanceOf[Fragment]

  def getByRelations(rel: RelationIds[Server]): Future[List[Server]] = db.run(
    (baseSelect ++ Fragments.whereAndOpt(
      rel
        .get(Relations.serverByNetworkId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"networkid".asInstanceOf[Fragment], _))
    )).query[Server].list
  )
}
