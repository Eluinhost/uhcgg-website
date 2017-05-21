package gg.uhc.website.schema.model

import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("UserRole")
@GraphQLDescription("Maps users to roles")
case class UserRole(userId: UUID, roleId: Int)
