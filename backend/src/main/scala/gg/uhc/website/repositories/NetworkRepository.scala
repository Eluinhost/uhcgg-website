package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Network

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object NetworkRepository {
  private[this] val baseSelect =
    fr"SELECT id, name, tag, description, created, modified, deleted, owner FROM networks".asInstanceOf[Fragment]

  val getAllQuery: Query0[Network] = baseSelect.query[Network]

  def getByIdsQuery(ids: NonEmptyList[Long]): Query0[Network] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Network]

  def relationsQuery(ownerIds: Option[NonEmptyList[UUID]]): Query0[Network] =
    (baseSelect ++ Fragments.whereOrOpt(
      ownerIds.map(ids ⇒ Fragments.in(fr"owner".asInstanceOf[Fragment], ids))
    )).query[Network]
}

class NetworkRepository(db: DatabaseRunner) extends RepositorySupport {
  import NetworkRepository._
  import db.Implicits._

  def getAll: Future[List[Network]] = getAllQuery.list.runOnDatabase

  def getByIds(ids: Seq[Long]): Future[List[Network]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }

  def getByRelations(rel: RelationIds[Network]): Future[List[Network]] =
    relationsQuery(
      ownerIds = rel.get(Relations.networkByUserId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
