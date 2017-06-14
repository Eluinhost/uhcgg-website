package gg.uhc.website.schema.definitions

import doobie.imports.ConnectionIO
import gg.uhc.website.model._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.scalars.{InetAddressScalarTypeSupport, InstantScalarTypeSupport}
import sangria.relay._
import sangria.schema._

import scalaz.Scalaz._

trait SchemaDefinition[T] {
  val Type: ObjectType[Unit, T]
}

trait SchemaQueries {
  val queries: List[Field[SchemaContext, Unit]]
}

trait SchemaSupport extends InetAddressScalarTypeSupport with InstantScalarTypeSupport {
  import scala.language.implicitConversions

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
  def idFields[T: Identifiable]: List[Field[Unit, T]] = fields[Unit, T](
    Node.globalIdField[Unit, T],
    Field(
      name = "rawId",
      fieldType = StringType,
      resolve = ctx ⇒ implicitly[Identifiable[T]].id(ctx.value),
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
