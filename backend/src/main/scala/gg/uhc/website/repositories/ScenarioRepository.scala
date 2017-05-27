package gg.uhc.website.repositories

import gg.uhc.website.schema.definitions.Relations
import gg.uhc.website.schema.model.Scenario
import sangria.execution.deferred.RelationIds

class ScenarioRepository
    extends Repository[Scenario]
    with CanQuery[Scenario]
    with CanQueryByIds[Long, Scenario]
    with CanQueryAll[Scenario]
    with CanQueryByRelations[Scenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Scenario] = implicitly
  override val idParam: Param[Long]           = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, name, description, created, modified, deleted, owner FROM scenarios"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Scenario]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.scenarioByOwnerId, "owner")
    )
}
