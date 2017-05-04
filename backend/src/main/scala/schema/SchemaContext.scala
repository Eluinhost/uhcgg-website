package schema

import repositories.{BanRepository, RoleRepository, UserRepository}

case class SchemaContext(users: UserRepository, roles: RoleRepository, bans: BanRepository)
