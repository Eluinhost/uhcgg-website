package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.database.DatabaseRunner
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Match

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object MatchRepository {
  private[this] val baseSelect =
    fr"SELECT id, host, serverid, versionid, styleid, size, created, modified, deleted, starts FROM matches"
      .asInstanceOf[Fragment]

  val getAllQuery: Query0[Match] = baseSelect.query[Match]

  def getByIdsQuery(ids: NonEmptyList[Long]): Query0[Match] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Match]

  def relationsQuery(
      hostIds: Option[NonEmptyList[UUID]],
      serverIds: Option[NonEmptyList[Long]],
      styleIds: Option[NonEmptyList[Int]],
      versionIds: Option[NonEmptyList[Int]]
    ): Query0[Match] =
    (baseSelect ++ Fragments.whereOrOpt(
      hostIds.map(ids ⇒ Fragments.in(fr"host".asInstanceOf[Fragment], ids)),
      serverIds.map(ids ⇒ Fragments.in(fr"serverid".asInstanceOf[Fragment], ids)),
      styleIds.map(ids ⇒ Fragments.in(fr"styleid".asInstanceOf[Fragment], ids)),
      versionIds.map(ids ⇒ Fragments.in(fr"versionid".asInstanceOf[Fragment], ids))
    )).query[Match]
}

class MatchRepository(db: DatabaseRunner) extends RepositorySupport {
  import MatchRepository._
  import db.Implicits._

  def getByIds(ids: Seq[Long]): Future[List[Match]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }

  def getAll: Future[List[Match]] = getAllQuery.list.runOnDatabase

  def getByRelations(rel: RelationIds[Match]): Future[List[Match]] =
    relationsQuery(
      hostIds = rel.get(Relations.matchByHostId).flatMap(_.toList.toNel),
      serverIds = rel.get(Relations.matchByServerId).flatMap(_.toList.toNel),
      styleIds = rel.get(Relations.matchByStyleId).flatMap(_.toList.toNel),
      versionIds = rel.get(Relations.matchByVersionId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
