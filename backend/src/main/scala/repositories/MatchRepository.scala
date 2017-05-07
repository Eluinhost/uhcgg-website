package repositories

import database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import schema.definitions.Relations
import schema.model.Match

import scala.concurrent.Future
import scalaz.Scalaz._

class MatchRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect =
    fr"SELECT id, host, serverid, versionid, styleid, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  def getByIds(ids: Seq[Long]): Future[List[Match]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      db.run(
        (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
          .query[Match]
          .list
      )
  }

  def getAll: Future[List[Match]] = db.run(baseSelect.query[Match].list)

  def getByRelations(rel: RelationIds[Match]): Future[List[Match]] =
    db.run(
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
      )).query[Match].list
    )
}
