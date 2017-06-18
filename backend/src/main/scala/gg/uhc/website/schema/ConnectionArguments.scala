package gg.uhc.website.schema

import sangria.schema._

/**
  * Arguemnts to a connection query
  *
  * @param first how many records to fetch
  * @param after only show items after the given cursor
  * @tparam C the cursor type
  */
case class ConnectionArguments[C](first: Int = 50, after: Option[C])

object ConnectionArguments {
  val First = Argument(name = "first", argumentType = OptionInputType(IntType), defaultValue = 50)
  def After[Cursor: ScalarType] =
    Argument(name = "after", argumentType = OptionInputType(implicitly[ScalarType[Cursor]]))

  def All[Cursor: ScalarType]: List[Argument[_ >: Int with Option[Cursor]]] =
    First :: After[Cursor] :: Nil

  def apply[Cursor: ScalarType](args: WithArguments): ConnectionArguments[Cursor] =
    new ConnectionArguments(
      first = args arg First,
      after = args arg After[Cursor]
    )
}
