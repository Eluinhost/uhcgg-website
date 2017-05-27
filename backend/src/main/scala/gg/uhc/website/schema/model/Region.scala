package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Region {
  implicit val hasId: HasId[Region, Int] = HasId(_.id)
}

@GraphQLName("Region")
@GraphQLDescription("A choosable region for hosting in")
case class Region(
    @GraphQLDescription("The unique ID of this region") id: Int,
    @GraphQLDescription("The 'short' verison of the name") short: String,
    @GraphQLDescription("The 'full' version of the name") long: String)
    extends IdentifiableModel[Int]
