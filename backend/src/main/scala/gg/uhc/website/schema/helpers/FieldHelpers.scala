package gg.uhc.website.schema.helpers

import gg.uhc.website.model.{BaseNode, DeleteableFields, ModificationTimesFields}
import gg.uhc.website.schema.scalars.InstantScalarTypeSupport._
import gg.uhc.website.schema.scalars.UuidScalarTypeSupport._
import sangria.relay.Node
import sangria.schema.{BooleanType, Field}

import scalaz.Scalaz._

object FieldHelpers extends FieldHelpers

trait FieldHelpers {
  def deletedField[Ctx, A <: DeleteableFields]: Field[Ctx, A] = Field(
    name = "deleted",
    fieldType = BooleanType,
    description = "Whether this item has been deleted or not".some,
    resolve = _.value.deleted
  )

  def globalIdField[Ctx, T <: BaseNode]: Field[Ctx, T] = Node.globalIdField

  def rawIdField[Ctx, T <: BaseNode]: Field[Ctx, T] = Field(
    name = "rawId",
    fieldType = UuidType,
    resolve = ctx ⇒ ctx.value.uuid,
    description = "The raw unique ID of this item".some
  )

  def modifiedField[Ctx, T <: ModificationTimesFields]: Field[Ctx, T] = Field(
    name = "modified",
    fieldType = InstantType,
    description = "When this item was last edited".some,
    resolve = _.value.modified
  )

  def createdField[Ctx, T <: ModificationTimesFields]: Field[Ctx, T] = Field(
    name = "created",
    fieldType = InstantType,
    description = "When this item was first created".some,
    resolve = _.value.created
  )
}
