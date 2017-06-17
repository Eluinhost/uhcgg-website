package gg.uhc.website.schema.helpers

import sangria.schema.{Argument, Context}

object ArgumentConverters extends ArgumentConverters

trait ArgumentConverters {
  implicit class ArgumentOps[A](argument: Argument[A])(implicit ctx: Context[_, _]) {
    def resolve: A = ctx.arg(argument)
  }
}
