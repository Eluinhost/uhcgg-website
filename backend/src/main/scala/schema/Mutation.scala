package schema

import schema.model.User

import scala.concurrent.Future


trait Mutation {
  this: SchemaContext ⇒

  def generateFakeUser(): Future[User] = users.createUser("test", "test@example.com", "test-password")
}
