package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{MatchScenario, Scenario}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._

import sangria.schema._

import scalaz.Scalaz._

object ScenarioSchema extends HasSchemaType[Scenario] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "scenarios",
      ListType(Type),
      arguments = Nil,// TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.scenarios.getAll,
      description = "Fetches all scenarios".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Scenario] = ObjectType[SchemaContext, Scenario](
    name = "Scenario",
    description = "Information about a specific scenario",
    interfaces = interfaces[SchemaContext, Scenario](RelaySchema.nodeInterface),
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
          fieldType = UserSchema.Type,
          description = "The owner of this scenario".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.ownerUserId)
        ),
        // Connections below here
        simpleConnectionField[Scenario, MatchScenario](
          name = "matches",
          target = MatchScenarioSchema.Type,
          description = "Matches with this scenario",
          action = _.matchScenarios.getByScenarioId,
          cursorFn = _.matchId.toString
        )
    )
  )
}
