package gg.uhc.website.security

import java.time.Instant
import java.util.UUID

import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.JwtSecret
import gg.uhc.website.schema.model.{Role, User}
import io.circe.parser.parse
import pdi.jwt.{JwtCirce, JwtClaim}
import pdi.jwt.algorithms.JwtHmacAlgorithm

import io.circe.syntax._

object ApiSession {
  case class Data(userId: UUID, username: String, email: String, permissions: Seq[String], roles: Seq[String])

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

class ApiSession(jwtSecret: String @@ JwtSecret, jwtAlgorithm: JwtHmacAlgorithm) extends CustomJsonCodec {
  def parseDataFromToken(token: String): Option[ApiSession.Data] =
    (for {
      claim   ← JwtCirce.decode(token, jwtSecret, Seq(jwtAlgorithm)).toEither.right
      json    ← parse(claim.content).right
      session ← json.as[ApiSession.Data].right
    } yield session).toOption

  def generateToken(user: User, roles: Seq[Role]): String = {
    val claim = JwtClaim(
      content = ApiSession.Data(user, roles).asJson.noSpaces,
      expiration = Some(Instant.now.plusSeconds(3600).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )

    JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
  }
}