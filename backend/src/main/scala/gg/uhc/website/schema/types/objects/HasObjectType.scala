package gg.uhc.website.schema.types.objects

import gg.uhc.website.schema.SchemaContext
import sangria.schema._

trait HasObjectType[T] {
  val Type: ObjectType[SchemaContext, T]
}