package gg.uhc.website.schema.types.scalars

import java.util.UUID

import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation
import scalaz.Scalaz._

import scala.util.Try

trait UuidScalarType {
  case object UuidCoercionViolation extends ValueCoercionViolation("UUID value expected")
  implicit val UuidType: ScalarType[UUID] = ScalarType[UUID](
    "UUID",
    description = "A unique identifier for an object".some,
    coerceOutput = (t: UUID, _) ⇒ t.toString,
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
}

object UuidScalarType extends UuidScalarType