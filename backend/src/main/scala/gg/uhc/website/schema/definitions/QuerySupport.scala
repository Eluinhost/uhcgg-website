package gg.uhc.website.schema.definitions

import doobie.imports.ConnectionIO
import gg.uhc.website.schema.SchemaContext
import sangria.schema.{Argument, Context, ReduceAction}

trait QuerySupport {
  implicit def connectionIO2FutureAction[A](
      value: ConnectionIO[A]
    )(implicit ctx: Context[SchemaContext, _]
    ): ReduceAction[SchemaContext, A] =
    ctx.ctx.run(value)

  implicit class ArgumentOps[A](argument: Argument[A])(implicit ctx: Context[_, _]) {
    def resolve: A = ctx.arg(argument)
  }
}
