package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object ScenarioQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "scenarioById",
      fieldType = OptionType(Types.ScenarioType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.scenarios.deferOpt(idArg.resolve),
      description = Some("Looks up a scenario with the given id")
    ),
    Field(
      name = "scenariosByIds",
      fieldType = ListType(Types.ScenarioType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.scenarios.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up scenarios with the given ids")
    ),
    Field(
      "scenarios",
      ListType(Types.ScenarioType),
      arguments = Nil, // TODO pagination + filters
      resolve = implicit ctx ⇒ ctx.ctx.scenarios.getAll,
      description = Some("Fetches all scenarios")
    )
  )
}
