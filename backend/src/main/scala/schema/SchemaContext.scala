package schema

import repositories.{BanRepository, RoleRepository, UserRepository, UserRolesRepository}

case class SchemaContext(
    users: UserRepository,
    roles: RoleRepository,
    bans: BanRepository,
    userRoles: UserRolesRepository)
