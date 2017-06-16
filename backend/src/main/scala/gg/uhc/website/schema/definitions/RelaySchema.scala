package gg.uhc.website.schema.definitions
import java.util.UUID

import gg.uhc.website.schema.SchemaContext
import sangria.relay.{GlobalId, Node, NodeDefinition}
import sangria.schema._

import scala.util.Try

object RelaySchema extends SchemaQueries {
  val resolveGlobalId: (GlobalId, Context[SchemaContext, Unit]) ⇒ LeafAction[SchemaContext, Option[Node]] =
    (globalId, _) ⇒
      Try {
        UUID.fromString(globalId.id)
      }.fold(
        _ ⇒ Value(None), // If we can't parse it as a UUID it isn't a valid ID
        uuid ⇒
          // Match each item by the item name for lookups, return None for other types
          globalId.typeName match {
            case "Ban" ⇒
              Fetchers.bans.deferOpt(uuid)
            case "Match" ⇒
              Fetchers.matches.deferOpt(uuid)
            case "Network" ⇒
              Fetchers.networks.deferOpt(uuid)
            case "Region" ⇒
              Fetchers.regions.deferOpt(uuid)
            case "Role" ⇒
              Fetchers.roles.deferOpt(uuid)
            case "Scenario" ⇒
              Fetchers.scenarios.deferOpt(uuid)
            case "Server" ⇒
              Fetchers.servers.deferOpt(uuid)
            case "Style" ⇒
              Fetchers.styles.deferOpt(uuid)
            case "User" ⇒
              Fetchers.users.deferOpt(uuid)
            case "Version" ⇒
              Fetchers.versions.deferOpt(uuid)
            case _ ⇒
              Value(None)
        }
    )

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
