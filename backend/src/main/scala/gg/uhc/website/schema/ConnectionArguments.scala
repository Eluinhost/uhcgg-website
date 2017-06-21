package gg.uhc.website.schema

import sangria.schema._

object ConnectionArguments {
  val First = Argument(name = "first", argumentType = OptionInputType(IntType), defaultValue = 50)
  def After[Cursor: ScalarType] =
    Argument(name = "after", argumentType = OptionInputType(implicitly[ScalarType[Cursor]]))

  def All[Cursor: ScalarType]: List[Argument[_ >: Int with Option[Cursor]]] =
    First :: After[Cursor] :: Nil

  def apply[Cursor: ScalarType](args: WithArguments): (Int, Option[Cursor]) =
    args.arg(First) → args.arg(After[Cursor])
}
