package schema

import java.time.Instant
import java.util.UUID

import sangria.ast
import sangria.marshalling.MarshallerCapability
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation

import scala.util.Try

object CustomScalars {
  private[this] def toStringOutput[T](t: T, capabilities: Set[MarshallerCapability]) = t.toString

  case object UuidCoercionViolation extends ValueCoercionViolation("UUID value expected")
  implicit val UuidType: ScalarType[UUID] = ScalarType[UUID](
    "UUID",
    description = Some("A unique identifier for an object"),
    coerceOutput = toStringOutput,
    coerceUserInput = {
      case s: String ⇒
        Try { UUID.fromString(s) }.toEither.left.map(_ ⇒ UuidCoercionViolation)
      case _ ⇒
        Left(UuidCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _) ⇒
        Try { UUID.fromString(s) }.toEither.left.map(_ ⇒ UuidCoercionViolation)
      case _ ⇒
        Left(UuidCoercionViolation)
    }
  )

  private[this] def tryConvertLongToInstant(l: Long) =
    Try { Instant.ofEpochMilli(l) }.toEither.left.map(_ ⇒ DateCoercionViolation)

  case object DateCoercionViolation extends ValueCoercionViolation("Timestamp expected")
  implicit val DateType: ScalarType[Instant] = ScalarType[Instant](
    "Date",
    description = Some("A timestamp"),
    coerceOutput = (i, _) ⇒ i.toEpochMilli,
    coerceInput = {
      case ast.BigIntValue(i, _, _) ⇒
        Try { Instant.ofEpochMilli(i.longValue()) }.toEither.left.map(_ ⇒ DateCoercionViolation)
      case _ ⇒
        Left(DateCoercionViolation)
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
