package gg.uhc.website.schema.types.objects

import gg.uhc.website.model.MatchScenario
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema.{Field, ObjectType, fields}

import scalaz.Scalaz._

object MatchScenarioObjectType extends HasObjectType[MatchScenario] {
  // TODO should be an edge type, not realy needed?
  override lazy val Type: ObjectType[SchemaContext, MatchScenario] = ObjectType[SchemaContext, MatchScenario](
    name = "MatchScenario",
    description = "Connects matches ↔ scenarios",
    fieldsFn = () ⇒
      fields[SchemaContext, MatchScenario](
        Field(
          name = "match",
          fieldType = MatchObjectType.Type,
          description = "The associated match".some,
          resolve = ctx ⇒ Fetchers.matches.defer(ctx.value.matchId)
        ),
        Field(
          name = "scenario",
          fieldType = ScenarioObjectType.Type,
          description = "The associated scenario".some,
          resolve = ctx ⇒ Fetchers.scenarios.defer(ctx.value.scenarioId)
        )
    )
  )
}
