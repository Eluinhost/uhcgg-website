package gg.uhc.website.schema.model

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Style")
@GraphQLDescription("A team style for a match")
case class Style(
    @GraphQLDescription("The unique ID for this style") id: Int,
    @GraphQLDescription("The 'short' template of this style") shortName: String,
    @GraphQLDescription("The 'full' template of this style") fullName: String,
    @GraphQLDescription("The full description explaining this style") description: String,
    @GraphQLDescription("Whether this style requires the size to be provided or not") requiresSize: Boolean)