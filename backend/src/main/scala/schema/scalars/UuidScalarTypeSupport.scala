package schema.scalars

import java.util.UUID

import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation

import scala.util.Try

trait UuidScalarTypeSupport {
  case object UuidCoercionViolation extends ValueCoercionViolation("UUID value expected")
  implicit val UuidType: ScalarType[UUID] = ScalarType[UUID](
    "UUID",
    description = Some("A unique identifier for an object"),
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
