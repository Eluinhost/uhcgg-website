package gg.uhc.website.schema.model

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Role")
@GraphQLDescription("A website role")
case class Role(
    @GraphQLDescription("The unique ID of this role") id: Int,
    @GraphQLDescription("The unique username of this role") name: String,
    @GraphQLDescription("The granted permissions for this role") permissions: List[String])
