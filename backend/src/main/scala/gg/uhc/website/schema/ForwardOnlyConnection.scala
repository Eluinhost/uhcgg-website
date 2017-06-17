package gg.uhc.website.schema

import sangria.schema.{Argument, IntType, OptionInputType, StringType, WithArguments}

object ForwardOnlyConnection {
  object Args {
    val First = Argument(name = "first", argumentType = OptionInputType(IntType), defaultValue = 50)
    val After = Argument(name = "after", argumentType = OptionInputType(StringType))

    val All: List[Argument[_]] = First :: After :: Nil
  }

  def apply(args: WithArguments): ForwardOnlyConnection =
    ForwardOnlyConnection(
      args arg ForwardOnlyConnection.Args.First,
      args arg ForwardOnlyConnection.Args.After
    )

  val empty = ForwardOnlyConnection()
}

case class ForwardOnlyConnection(first: Long = 50, after: Option[String] = None) // TODO limits for first
