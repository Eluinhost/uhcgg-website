package repositories

import database.DatabaseService
import doobie.imports.{Fragment, _}
import sangria.execution.deferred.RelationIds
import schema.definitions.Relations
import schema.model.MatchScenario

import scala.concurrent.Future
import scalaz.Scalaz._

class MatchScenariosRepository(db: DatabaseService) extends RepositorySupport {
  private[this] val baseSelect = fr"SELECT matchid, scenarioid FROM match_scenarios".asInstanceOf[Fragment]

  def getByRelations(rel: RelationIds[MatchScenario]): Future[List[MatchScenario]] =
    db.run(
      (baseSelect ++ Fragments.whereOrOpt(
        rel
          .get(Relations.matchScenarioByMatchId)
          .flatMap(_.toList.toNel) // convert to a non-empty list first
          .map(Fragments.in(fr"matchid".asInstanceOf[Fragment], _)),
        rel
          .get(Relations.matchScenarioByScenarioId)
          .flatMap(_.toList.toNel)
          .map(Fragments.in(fr"scenarioid".asInstanceOf[Fragment], _))
      )).query[MatchScenario].list
    )
}