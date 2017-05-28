package gg.uhc.website.schema.scalars

import java.time.Instant

import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation
import scalaz.Scalaz._

import scala.util.Try

trait InstantScalarTypeSupport {
  private[this] def tryConvertLongToInstant(l: Long) =
    Try { Instant.ofEpochMilli(l) }.toEither.left.map(_ ⇒ DateCoercionViolation)

  case object DateCoercionViolation extends ValueCoercionViolation("Timestamp expected")

  implicit val DateType: ScalarType[Instant] = ScalarType[Instant](
    "Date",
    description = "A timestamp".some,
    coerceOutput = (i, _) ⇒ i.toEpochMilli,
    coerceInput = {
      case ast.BigIntValue(i, _, _) ⇒ tryConvertLongToInstant(i.longValue())
      case _                        ⇒ Left(DateCoercionViolation)
    },
    coerceUserInput = {
      case i: Int                      ⇒ tryConvertLongToInstant(i: Long)
      case i: Long                     ⇒ tryConvertLongToInstant(i)
      case i: BigInt if !i.isValidLong ⇒ Left(DateCoercionViolation)
      case i: BigInt                   ⇒ tryConvertLongToInstant(i.longValue)
      case _                           ⇒ Left(DateCoercionViolation)
    }
  )
}
