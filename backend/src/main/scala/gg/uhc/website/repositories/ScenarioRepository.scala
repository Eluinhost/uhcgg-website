package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Scenario

import scala.concurrent.Future
import scalaz.Scalaz._

class ScenarioRepository(db: DatabaseRunner) extends RepositorySupport {
  import db.Implicits._

  private[this] val baseSelect =
    fr"SELECT id, name, description, created, modified, deleted, owner FROM scenarios"
      .asInstanceOf[Fragment]

  def getByIds(ids: Seq[Long]): Future[List[Scenario]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
        .query[Scenario]
        .list
        .runOnDatabase
  }

  def getAll: Future[List[Scenario]] = baseSelect.query[Scenario].list.runOnDatabase

  def getByRelations(rel: RelationIds[Scenario]): Future[List[Scenario]] =
    (baseSelect ++ Fragments.whereAndOpt(
      rel
        .get(Relations.scenarioByOwnerId)
        .flatMap(_.toList.toNel) // convert to a non-empty list first
        .map(Fragments.in(fr"owner".asInstanceOf[Fragment], _))
    )).query[Scenario].list.runOnDatabase
}
