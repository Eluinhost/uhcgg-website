package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Role {
  implicit val hasId: HasId[Role, Int] = HasId(_.id)
}

@GraphQLName("Role")
@GraphQLDescription("A website role")
case class Role(
    @GraphQLDescription("The unique ID of this role") id: Int,
    @GraphQLDescription("The unique username of this role") name: String,
    @GraphQLDescription("The granted permissions for this role") permissions: List[String])
    extends IdentifiableModel[Int]