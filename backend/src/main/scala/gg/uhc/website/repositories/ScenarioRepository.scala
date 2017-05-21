package gg.uhc.website.repositories

import gg.uhc.website.database.DatabaseService
import doobie.imports.{Fragment, _}
import doobie.postgres.imports._
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Scenario

import scala.concurrent.Future
import scalaz.Scalaz._

class ScenarioRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect =
    fr"SELECT id, name, description, created, modified, deleted, owner FROM scenarios"
      .asInstanceOf[Fragment]

  def getByIds(ids: Seq[Long]): Future[List[Scenario]] = ids.toList.toNel match {
    case None ⇒ Future successful List()
    case Some(nel) ⇒
      db.run(
        (baseSelect ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], nel)))
          .query[Scenario]
          .list
      )
  }

  def getAll: Future[List[Scenario]] = db.run(baseSelect.query[Scenario].list)

  def getByRelations(rel: RelationIds[Scenario]): Future[List[Scenario]] =
    db.run(
      (baseSelect ++ Fragments.whereAndOpt(
        rel
          .get(Relations.scenarioByOwnerId)
          .flatMap(_.toList.toNel) // convert to a non-empty list first
          .map(Fragments.in(fr"owner".asInstanceOf[Fragment], _))
      )).query[Scenario].list
    )
}
