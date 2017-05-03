package schema

import repositories.{RoleRepository, UserRepository}

case class SchemaContext(users: UserRepository, roles: RoleRepository)
