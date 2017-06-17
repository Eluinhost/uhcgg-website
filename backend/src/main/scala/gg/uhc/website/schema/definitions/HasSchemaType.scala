package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

trait HasSchemaType[T] {
  val Type: ObjectType[SchemaContext, T]
}