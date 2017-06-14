package gg.uhc.website.repositories

import gg.uhc.website.model.Scenario
import gg.uhc.website.schema.definitions.Relations
import sangria.execution.deferred.RelationIds

class ScenarioRepository
    extends Repository[Scenario]
    with CanQuery[Scenario]
    with CanQueryByIds[Scenario]
    with CanQueryAll[Scenario]
    with CanQueryByRelations[Scenario] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Scenario] = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT uuid, name, description, created, modified, deleted, ownerUserId FROM scenarios"
      .asInstanceOf[Fragment]

  override def relationsFragment(relationIds: RelationIds[Scenario]): Fragment =
    Fragments.whereOrOpt(
      simpleRelationFragment(relationIds, Relations.scenarioByOwnerId, "ownerUserId", "uuid")
    )
}
