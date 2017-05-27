package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Version {
  implicit val hasId: HasId[Version, Int] = HasId(_.id)
}

@GraphQLName("Version")
@GraphQLDescription("A choosable version for hosting")
case class Version(
    @GraphQLDescription("The unique ID of this version") id: Int,
    @GraphQLDescription("The display name of this version") name: String,
    @GraphQLDescription("Whether the item is live or not. Only live versions can be used for a new match") live: Boolean)
    extends IdentifiableModel[Int]