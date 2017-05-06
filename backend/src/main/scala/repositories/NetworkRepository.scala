package repositories

import database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import schema.definitions.Relations
import schema.model.Network

import scala.concurrent.Future
import scalaz.Scalaz._

class NetworkRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT id, name, tag, description, created, modified, deleted, owner FROM networks".asInstanceOf[Fragment]

  def getAll: Future[List[Network]] = db.run(baseSelect.query[Network].list)

  def getByIds(ids: Seq[Long]): Future[List[Network]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒ db.run(
      (
        baseSelect
          ++ Fragments.whereAnd(
            Fragments.in(fr"id".asInstanceOf[Fragment], nel)
          )
      ).query[Network].list
    )
  }

  def getByRelations(rel: RelationIds[Network]): Future[List[Network]] = db.run(
    (baseSelect ++ Fragments.whereAndOpt(
      rel
        .get(Relations.networkByUserId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"owner".asInstanceOf[Fragment], _))
    )).query[Network].list
  )
}
