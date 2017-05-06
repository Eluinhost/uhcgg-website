package schema.definitions

import sangria.schema._
import schema.SchemaContext

class StyleQueries {
  val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  val query: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "styleById",
      fieldType = OptionType(Types.StyleType),
      arguments = idArg :: Nil,
      resolve = ctx ⇒ Fetchers.styles.deferOpt(ctx arg idArg),
      description = Some("Looks up a style with the given id")
    ),
    Field(
      name = "stylesByIds",
      fieldType = ListType(Types.StyleType),
      arguments = idsArg :: Nil,
      resolve = ctx ⇒ Fetchers.styles.deferSeqOpt(ctx arg idsArg),
      description = Some("Looks up styles with the given ids")
    ),
    Field(
      "styles",
      ListType(Types.StyleType),
      arguments = Nil,
      resolve = ctx ⇒ ctx.ctx.styles.getAll,
      description = Some("Fetches all styles")
    )
  )
}
