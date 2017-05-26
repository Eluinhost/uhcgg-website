package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Network

import scala.concurrent.Future
import scalaz.Scalaz._

class NetworkRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect =
    fr"SELECT id, name, tag, description, created, modified, deleted, owner FROM networks".asInstanceOf[Fragment]

  def getAll: Future[List[Network]] = baseSelect.query[Network].list.runOnDatabase

  def getByIds(ids: Seq[Long]): Future[List[Network]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(
        Fragments.in(fr"id".asInstanceOf[Fragment], nel)
      )).query[Network].list.runOnDatabase
  }

  def getByRelations(rel: RelationIds[Network]): Future[List[Network]] =
    (baseSelect ++ Fragments.whereAndOpt(
      rel
        .get(Relations.networkByUserId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"owner".asInstanceOf[Fragment], _))
    )).query[Network].list.runOnDatabase
}
