package gg.uhc.website.schema.model

import java.time.Instant
import java.util.UUID

import sangria.macros.derive.{GraphQLDescription, GraphQLExclude, GraphQLName}

@GraphQLName("User")
@GraphQLDescription("A website account")
case class User(
    @GraphQLDescription("The unique ID of this user") id: UUID,
    @GraphQLDescription("The unique username of this user") username: String,
    @GraphQLExclude email: String,
    @GraphQLExclude password: String,
    @GraphQLDescription("The time the account was created") created: Instant)
