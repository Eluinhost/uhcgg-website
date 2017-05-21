package gg.uhc.website.schema.model

import sangria.macros.derive.{GraphQLDescription, GraphQLName}

@GraphQLName("MatchScenario")
@GraphQLDescription("Maps matches to scenarios")
case class MatchScenario(matchId: Long, scenarioId: Long)
