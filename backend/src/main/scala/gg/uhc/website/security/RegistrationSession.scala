package gg.uhc.website.security

import java.time.{Duration, Instant}
import java.util.UUID

import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.{JwtSecret, RegistrationJwtDuration}
import gg.uhc.website.security.RegistrationSession._
import io.circe.JsonObject
import io.circe.parser.parse
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}
import io.circe.syntax._

import scalaz.Scalaz._

object RegistrationSession {
  case class Data(username: Option[String])
  case class Error(header: String, message: String)
}

class RegistrationSession(
    jwtSecret: String @@ JwtSecret,
    jwtAlgorithm: JwtHmacAlgorithm,
    registrationTimeout: Duration @@ RegistrationJwtDuration)
    extends CustomJsonCodec {
  def parseDataFromToken(token: String): Option[RegistrationSession.Data] =
    (for {
      claim   ← JwtCirce.decode(token, jwtSecret, Seq(jwtAlgorithm)).toEither.right
      json    ← parse(claim.content).right
      session ← json.as[RegistrationSession.Data].right
    } yield session).toOption

  def generateStage1Token()                 = generateUsernameToken(username = none)
  def generateStage2Token(username: String) = generateUsernameToken(username = username.some)

  private def generateToken(data: JsonObject) = {
    val now = Instant.now()

    val claim = JwtClaim(
      content = data.add("state", UUID.randomUUID().toString.asJson).asJson.noSpaces,
      expiration = now.plus(registrationTimeout).getEpochSecond.some,
      issuedAt = now.getEpochSecond.some
    )

    JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
  }

  def generateErrorToken(header: String, message: String) =
    generateToken(Error(header, message).asJsonObject)

  private def generateUsernameToken(username: Option[String]): String =
    generateToken(Data(username).asJsonObject)
}
