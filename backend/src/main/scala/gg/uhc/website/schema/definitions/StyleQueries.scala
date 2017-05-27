package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

object StyleQueries extends QuerySupport {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "styleById",
      fieldType = OptionType(Types.StyleType),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.styles.deferOpt(idArg.resolve),
      description = Some("Looks up a style with the given id")
    ),
    Field(
      name = "stylesByIds",
      fieldType = ListType(Types.StyleType),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.styles.deferSeqOpt(idsArg.resolve),
      description = Some("Looks up styles with the given ids")
    ),
    Field(
      "styles",
      ListType(Types.StyleType), // TODO pagination
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.styles.getAll,
      description = Some("Fetches all styles")
    )
  )
}
