package gg.uhc.website.schema.definitions
import java.util.UUID

import gg.uhc.website.schema.SchemaContext
import sangria.relay.{GlobalId, Node, NodeDefinition}
import sangria.schema._

object RelaySchema extends SchemaQueries {
  val resolveGlobalId: (GlobalId, Context[SchemaContext, Unit]) ⇒ LeafAction[SchemaContext, Option[Node]] =
    (globalId, _) ⇒
      globalId.typeName match {
        case "Network" ⇒
          Fetchers.networks.deferOpt(UUID.fromString(globalId.id))
        case _ ⇒
          Value(None)
    }

  val NodeDefinition(nodeInterface, nodeField, nodesField) = Node.definition(
    resolve = resolveGlobalId,
    possibleTypes = Node.possibleNodeTypes[SchemaContext, Node](
      BanSchema.Type,
      MatchSchema.Type,
      NetworkSchema.Type,
      RegionSchema.Type,
      RoleSchema.Type,
      ScenarioSchema.Type,
      ServerSchema.Type,
      StyleSchema.Type,
      UserSchema.Type,
      VersionSchema.Type
    ),
    complexity = None,
    tags = Nil
  )

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    nodeField,
    nodesField
  )
}
