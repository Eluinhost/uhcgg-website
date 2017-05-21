package gg.uhc.website.schema

import gg.uhc.website.schema.model.User

import scala.concurrent.Future

trait Mutation { this: SchemaContext ⇒

  def generateFakeUser(): Future[User] = users.createUser("test", "test@example.com", "test-password")
}
