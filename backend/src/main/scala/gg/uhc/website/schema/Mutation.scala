package gg.uhc.website.schema

import java.util.UUID

import scala.concurrent.Future

trait Mutation { this: SchemaContext ⇒
  def login(username: String, password: String): Future[Option[String]] = users.authenticate(username, password)
  def changePassword(id: UUID, password: String): Future[Boolean] = users.changePassword(id, password)
}
