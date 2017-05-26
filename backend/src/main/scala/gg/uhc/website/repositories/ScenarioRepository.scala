package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Scenario

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object ScenarioRepository {
  private[this] val baseSelect =
    fr"SELECT id, name, description, created, modified, deleted, owner FROM scenarios"
      .asInstanceOf[Fragment]

  val getAllQuery: Query0[Scenario] = baseSelect.query[Scenario]

  def getByIdsQuery(ids: NonEmptyList[Long]): Query0[Scenario] =
    (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[Scenario]

  def relationsQuery(ownerIds: Option[NonEmptyList[UUID]]): Query0[Scenario] =
    (baseSelect ++ Fragments.whereOrOpt(
      ownerIds.map(ids ⇒ Fragments.in(fr"owner".asInstanceOf[Fragment], ids))
    )).query[Scenario]
}

class ScenarioRepository(db: DatabaseRunner) extends RepositorySupport {
  import ScenarioRepository._
  import db.Implicits._

  def getByIds(ids: Seq[Long]): Future[List[Scenario]] = ids.toList.toNel match {
    case None      ⇒ Future successful List()
    case Some(nel) ⇒ getByIdsQuery(nel).list.runOnDatabase
  }

  def getAll: Future[List[Scenario]] = getAllQuery.list.runOnDatabase

  def getByRelations(rel: RelationIds[Scenario]): Future[List[Scenario]] =
    relationsQuery(
      ownerIds = rel.get(Relations.scenarioByOwnerId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
