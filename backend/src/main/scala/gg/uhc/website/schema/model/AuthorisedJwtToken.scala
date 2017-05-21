package gg.uhc.website.schema.model

import java.util.UUID

case class AuthorisedJwtToken(userId: UUID, username: String, email: String, permissions: Seq[String], roles: Seq[String])

object AuthorisedJwtToken {
  def apply(user: User, roles: Seq[Role]): AuthorisedJwtToken =
    new AuthorisedJwtToken(
      userId = user.id,
      username = user.username,
      email = user.email,
      permissions = roles.flatMap(_.permissions).distinct,
      roles = roles.map(_.name).distinct
    )
}