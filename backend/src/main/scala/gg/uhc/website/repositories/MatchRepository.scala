package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseRunner
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Match

import scala.concurrent.Future
import scalaz.Scalaz._

class MatchRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect =
    fr"SELECT id, host, serverid, versionid, styleid, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  def getByIds(ids: Seq[Long]): Future[List[Match]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
        .query[Match]
        .list
        .runOnDatabase
  }

  def getAll: Future[List[Match]] = baseSelect.query[Match].list.runOnDatabase

  def getByRelations(rel: RelationIds[Match]): Future[List[Match]] =
    (baseSelect ++ Fragments.whereOrOpt(
      rel
        .get(Relations.matchByHostId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"host".asInstanceOf[Fragment], _)),
      rel
        .get(Relations.matchByServerId)
        .flatMap(_.toList.toNel)
        .map(Fragments.in(fr"serverid".asInstanceOf[Fragment], _)),
      rel
        .get(Relations.matchByStyleId)
        .flatMap(_.toList.toNel)
        .map(Fragments.in(fr"styleid".asInstanceOf[Fragment], _)),
      rel
        .get(Relations.matchByVersionId)
        .flatMap(_.toList.toNel)
        .map(Fragments.in(fr"versionid".asInstanceOf[Fragment], _))
    )).query[Match].list.runOnDatabase
}
