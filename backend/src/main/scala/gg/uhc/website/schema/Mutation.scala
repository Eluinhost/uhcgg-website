package gg.uhc.website.schema

import java.time.Instant
import java.util.UUID

import gg.uhc.website.CustomJsonCodec._
import gg.uhc.website.schema.model.AuthorisedJwtToken
import io.circe.syntax._
import pdi.jwt.{JwtCirce, JwtClaim}

import scala.concurrent.Future
import scalaz.OptionT._
import scalaz.std.scalaFuture.futureInstance

trait Mutation { this: SchemaContext ⇒
  def token(username: String, password: String): Future[Option[String]] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    (for {
      user      ← optionT(users.authenticate(username, password))
      userRoles ← optionT(userRoles.search(userIds = Some(Seq(user.id))).map(Option(_)))
      roles     ← optionT(roles.getByIds(userRoles.map(_.roleId)).map(Option(_)))
    } yield AuthorisedJwtToken(user, roles)).run.map {
      _.map { token ⇒
        val claim = JwtClaim(
          content = token.asJson.noSpaces,
          expiration = Some(Instant.now.plusSeconds(3600).getEpochSecond),
          issuedAt = Some(Instant.now.getEpochSecond)
        )

        JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
      }
    }
  }
  def changePassword(id: UUID, password: String): Future[Boolean] = users.changePassword(id, password)
}
