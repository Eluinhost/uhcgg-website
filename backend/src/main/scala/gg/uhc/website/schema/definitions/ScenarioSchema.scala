package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Scenario
import gg.uhc.website.schema.SchemaContext
import sangria.schema._

import scalaz.Scalaz._

object ScenarioSchema extends SchemaDefinition[Scenario] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = StringType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(StringType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "scenarioById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.scenarios.deferOpt(idArg.resolve),
      description = "Looks up a scenario with the given id".some
    ),
    Field(
      name = "scenariosByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.scenarios.deferSeqOpt(idsArg.resolve),
      description = "Looks up scenarios with the given ids".some
    ),
    Field(
      "scenarios",
      ListType(Type),
      arguments = Nil, // TODO pagination + filters
      resolve = implicit ctx ⇒ ctx.ctx.scenarios.getAll,
      description = "Fetches all scenarios".some
    )
  )

  override lazy val Type: ObjectType[Unit, Scenario] = ObjectType(
    name = "Scenario",
    description = "Information about a specific scenario",
    interfaces = interfaces[Unit, Scenario](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      idFields[Scenario] ++ modificationTimesFields ++ deletedFields ++ fields[Unit, Scenario](
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
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
        ),
        Field(
          name = "matches",
          fieldType = ListType(MatchScenarioSchema.Type),
          description = "Matches with this scenario".some, // TODO pagination + filtering
          resolve = ctx ⇒ Fetchers.matchScenarios.deferRelSeq(Relations.matchScenarioByScenarioId, ctx.value.id)
        )
    )
  )
}
