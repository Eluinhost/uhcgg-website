package gg.uhc.website.schema.definitions

import doobie.imports.ConnectionIO
import gg.uhc.website.model._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.scalars.{InetAddressScalarTypeSupport, InstantScalarTypeSupport, UuidScalarTypeSupport}
import sangria.relay._
import sangria.schema._

import scalaz.Scalaz._

trait SchemaDefinition[T] {
  val Type: ObjectType[Unit, T]
}

trait SchemaQueries {
  val queries: List[Field[SchemaContext, Unit]]
}

trait SchemaSupport extends InetAddressScalarTypeSupport with InstantScalarTypeSupport with UuidScalarTypeSupport {
  import scala.language.implicitConversions

  val idArg  = Argument(name = "id", argumentType = UuidType, description = "ID to match")
  val idsArg = Argument(name = "ids", argumentType = ListInputType(UuidType), description = "IDs to match")

  implicit def connectionIO2FutureAction[A](
      value: ConnectionIO[A]
    )(implicit ctx: Context[SchemaContext, _]
    ): ReduceAction[SchemaContext, A] =
    ctx.ctx.run(value)

  implicit class ArgumentOps[A](argument: Argument[A])(implicit ctx: Context[_, _]) {
    def resolve: A = ctx.arg(argument)
  }

  def deletedFields[A <: DeleteableFields]: List[Field[Unit, A]] = fields[Unit, A](
    Field(
      name = "deleted",
      fieldType = BooleanType,
      description = "Whether this item has been deleted or not".some,
      resolve = _.value.deleted
    )
  )

  /**
    * Generic ID field for all things with an ID
    */
  def idFields[T <: BaseNode]: List[Field[Unit, T]] = fields[Unit, T](
    Node.globalIdField[Unit, T],
    Field(
      name = "rawId",
      fieldType = UuidType,
      resolve = ctx ⇒ ctx.value.uuid,
      description = "The raw unique ID of this item".some
    )
  )

  /**
    * Add created/modified fields
    */
  def modificationTimesFields[A <: ModificationTimesFields]: List[Field[Unit, A]] =
    fields[Unit, A](
      Field(
        name = "modified",
        fieldType = DateType,
        description = "When this item was last edited".some,
        resolve = _.value.modified
      ),
      Field(
        name = "created",
        fieldType = DateType,
        description = "When this item was first created".some,
        resolve = _.value.created
      )
    )
}
