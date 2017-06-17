package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._
import scalaz.Scalaz._

object QueryMetadataSchema extends HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "complexity",
      fieldType = FloatType,
      description = "How complex the current query is".some,
      resolve = ctx ⇒ ctx.ctx.metadata.complexity.get // must exist if this is being resovled
    ),
    Field(
      name = "depth",
      fieldType = IntType,
      description = "How nested the current query is".some,
      resolve = ctx ⇒ ctx.ctx.metadata.depth.get // must exist if this is being resovled
    )
  )
}
