package gg.uhc.website.schema.model

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("Region")
@GraphQLDescription("A choosable region for hosting in")
case class Region(
    @GraphQLDescription("The unique ID of this region") id: Int,
    @GraphQLDescription("The 'short' verison of the name") short: String,
    @GraphQLDescription("The 'full' version of the name") long: String)
