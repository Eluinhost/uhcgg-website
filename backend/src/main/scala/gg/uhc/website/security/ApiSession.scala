package gg.uhc.website.security

import java.time.{Duration, Instant}

import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.{ApiJwtDuration, JwtSecret}
import gg.uhc.website.model.{Role, User}
import io.circe.parser.parse
import pdi.jwt.{JwtCirce, JwtClaim}
import pdi.jwt.algorithms.JwtHmacAlgorithm
import io.circe.syntax._

import scalaz.Scalaz._

object ApiSession {
  case class Data(userId: String, username: String, email: String, permissions: Seq[String], roles: Seq[String])

  object Data {
    def apply(user: User, roles: Seq[Role]): Data = new Data(
      userId = user.id,
      username = user.username,
      email = user.email,
      permissions = roles.flatMap(_.permissions).distinct,
      roles = roles.map(_.name).distinct
    )
  }
}

class ApiSession(
    jwtSecret: String @@ JwtSecret,
    jwtAlgorithm: JwtHmacAlgorithm,
    jwtTimeout: Duration @@ ApiJwtDuration)
    extends CustomJsonCodec {
  def parseDataFromToken(token: String): Option[ApiSession.Data] =
    (for {
      claim   ← JwtCirce.decode(token, jwtSecret, Seq(jwtAlgorithm)).toEither.right
      json    ← parse(claim.content).right
      session ← json.as[ApiSession.Data].right
    } yield session).toOption

  def generateToken(user: User, roles: Seq[Role]): String = {
    val now = Instant.now()

    val claim = JwtClaim(
      content = ApiSession.Data(user, roles).asJson.noSpaces,
      expiration = now.plus(jwtTimeout).getEpochSecond.some,
      issuedAt = now.getEpochSecond.some
    )

    JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
  }
}
