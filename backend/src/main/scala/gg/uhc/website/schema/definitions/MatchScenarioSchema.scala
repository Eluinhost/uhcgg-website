package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.model.MatchScenario
import sangria.schema.{Field, ObjectType, fields}

import scalaz.Scalaz._

object MatchScenarioSchema extends SchemaDefinition[MatchScenario] with SchemaSupport {
  override lazy val Type: ObjectType[Unit, MatchScenario] = ObjectType(
    name = "MatchScenario",
    description = "Connects matches ↔ scenarios",
    fieldsFn = () ⇒
      fields[Unit, MatchScenario](
        Field(
          name = "match",
          fieldType = MatchSchema.Type,
          description = "The associated match".some,
          resolve = ctx ⇒ Fetchers.matches.defer(ctx.value.matchId)
        ),
        Field(
          name = "scenario",
          fieldType = ScenarioSchema.Type,
          description = "The associated scenario".some,
          resolve = ctx ⇒ Fetchers.scenarios.defer(ctx.value.scenarioId)
        )
    )
  )
}
