package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema.Field

trait HasSchemaQueries {
  val queries: List[Field[SchemaContext, Unit]]
}

