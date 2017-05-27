package gg.uhc.website.schema.definitions

import doobie.imports.ConnectionIO
import gg.uhc.website.schema.SchemaContext
import sangria.schema.{Argument, Context, ReduceAction}

trait QuerySupport {
  implicit def connectionIO2FutureAction[Ctx, Val](
      value: ConnectionIO[Val]
    )(implicit ctx: Context[SchemaContext, _]
    ): ReduceAction[Ctx, Val] =
    ctx.ctx.run(value)

  implicit class ArgumentOps[A](argument: Argument[A])(implicit ctx: Context[_, _]) {
    def resolve: A = implicitly[Context[_,_]].arg(argument)
  }
}
