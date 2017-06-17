package gg.uhc.website.schema.helpers

import doobie.imports.ConnectionIO
import gg.uhc.website.schema.SchemaContext
import sangria.schema.{Context, ReduceAction}

import scala.concurrent.Future

object ConnectionIOConverters extends ConnectionIOConverters

trait ConnectionIOConverters {
  import scala.language.implicitConversions

  /**
    * Converts a ConnectionIO[A] to a Future[A] by running it via the implicit SchemaContext
    */
  implicit def connectionIO2Future[A](value: ConnectionIO[A])(implicit ctx: Context[SchemaContext, _]): Future[A] =
    ctx.ctx.run(value)

  /**
    * Converts a ConnectionIO[A] to a ReduceAction[SchemaContext, A] by
    * running it via the implicit SchemaContext
    */
  implicit def connectionIO2ReduceAction[A](
      value: ConnectionIO[A]
    )(implicit ctx: Context[SchemaContext, _]
    ): ReduceAction[SchemaContext, A] = ctx.ctx.run(value)
}
