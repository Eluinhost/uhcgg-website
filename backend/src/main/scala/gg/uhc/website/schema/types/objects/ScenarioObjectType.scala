package gg.uhc.website.schema.types.objects

import java.util.UUID

import gg.uhc.website.model.{MatchScenario, Scenario}
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema._

import scalaz.Scalaz._

object ScenarioObjectType extends HasObjectType[Scenario] {
  override lazy val Type: ObjectType[SchemaContext, Scenario] = ObjectType[SchemaContext, Scenario](
    name = "Scenario",
    description = "Information about a specific scenario",
    interfaces = interfaces[SchemaContext, Scenario](RelayDefinitions.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Scenario](
        globalIdField,
        rawIdField,
        modifiedField,
        createdField,
        deletedField,
        Field(
          name = "name",
          fieldType = StringType,
          description = "The title of this scenario".some,
          resolve = _.value.name
        ),
        Field(
          name = "description",
          fieldType = StringType,
          description = "Markdown description describing this scenario".some,
          resolve = _.value.description
        ),
        // relations below here
        Field(
          name = "owner",
          fieldType = UserObjectType.Type,
          description = "The owner of this scenario".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.ownerUserId)
        ),
        relationshipField[Scenario, MatchScenario, UUID, UUID](
          name = "matches",
          targetType = MatchScenarioObjectType.Type,
          description = "Matches with this scenario",
          action = _.matchScenarios.getByScenarioId,
          cursorFn = (ms: MatchScenario) ⇒ ms.matchId,
          idFn = (s: Scenario) ⇒ s.uuid
        )
    )
  )
}
