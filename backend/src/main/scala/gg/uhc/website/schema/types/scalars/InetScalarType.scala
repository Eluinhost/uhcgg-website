package gg.uhc.website.schema.types.scalars

import java.net.InetAddress

import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation
import scalaz.Scalaz._

import scala.util.Try

trait InetScalarType {
  private[this] def tryConvertStringToInet(s: String) =
    Try { InetAddress.getByName(s) }.toEither.left.map(_ ⇒ InetCoercionViolation)

  case object InetCoercionViolation extends ValueCoercionViolation("IP address expected")

  implicit val InetType: ScalarType[InetAddress] = ScalarType[InetAddress](
    "Inet",
    description = "An IP address".some,
    coerceOutput = (i, _) ⇒ i.toString,
    coerceInput = {
      case ast.StringValue(i, _, _) ⇒ tryConvertStringToInet(i)
      case _                        ⇒ Left(InetCoercionViolation)
    },
    coerceUserInput = {
      case s: String ⇒ tryConvertStringToInet(s)
      case _         ⇒ Left(InetCoercionViolation)
    }
  )
}

object InetScalarType extends InetScalarType