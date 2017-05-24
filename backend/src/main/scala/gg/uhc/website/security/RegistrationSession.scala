package gg.uhc.website.security

import java.time.Instant
import java.util.UUID

import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.JwtSecret
import io.circe.parser.parse
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}

import io.circe.syntax._

object RegistrationSession {
  case class Data(username: Option[String], randomState: String)
}

class RegistrationSession(jwtSecret: String @@ JwtSecret, jwtAlgorithm: JwtHmacAlgorithm) extends CustomJsonCodec {
  def parseDataFromToken(token: String): Option[RegistrationSession.Data] =
    (for {
      claim   ← JwtCirce.decode(token, jwtSecret, Seq(jwtAlgorithm)).toEither.right
      json    ← parse(claim.content).right
      session ← json.as[RegistrationSession.Data].right
    } yield session).toOption

  def generateStage1Token() = generateToken(username = None)
  def generateStage2Token(username: String) = generateToken(username = Some(username))

  private def generateToken(username: Option[String]): String = {
    val now = Instant.now()

    // used to improve entropy in generated token, we don't actually use/check it though so probably not even worth it
    val randomState = UUID.randomUUID().toString

    val claim = JwtClaim(
      content = RegistrationSession.Data(username, randomState).asJson.noSpaces,
      expiration = Some(now.plusSeconds(5 * 60).getEpochSecond), // TODO move into configuration
      issuedAt = Some(now.getEpochSecond)
    )

    JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
  }
}
