package gg.uhc.website.repositories

import doobie.imports.{Fragment, _}
import gg.uhc.website.database.DatabaseRunner
import sangria.execution.deferred.RelationIds
import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.MatchScenario

import scala.concurrent.Future
import scalaz.NonEmptyList
import scalaz.Scalaz._

object MatchScenariosRepository {
  private[this] val baseSelect = fr"SELECT matchid, scenarioid FROM match_scenarios".asInstanceOf[Fragment]

  def relationsQuery(
      matchIds: Option[NonEmptyList[Long]],
      scenarioIds: Option[NonEmptyList[Long]]
    ): Query0[MatchScenario] =
    (baseSelect ++ Fragments.whereOrOpt(
      matchIds.map(ids ⇒ Fragments.in(fr"matchid".asInstanceOf[Fragment], ids)),
      scenarioIds.map(ids ⇒ Fragments.in(fr"scenarioid".asInstanceOf[Fragment], ids))
    )).query[MatchScenario]

}

class MatchScenariosRepository(db: DatabaseRunner) extends RepositorySupport {
  import MatchScenariosRepository._
  import db.Implicits._

  def getByRelations(rel: RelationIds[MatchScenario]): Future[List[MatchScenario]] =
    relationsQuery(
      matchIds = rel.get(Relations.matchScenarioByMatchId).flatMap(_.toList.toNel),
      scenarioIds = rel.get(Relations.matchScenarioByScenarioId).flatMap(_.toList.toNel)
    ).list.runOnDatabase
}
