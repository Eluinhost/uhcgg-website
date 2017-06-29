package gg.uhc.website.schema.types.objects

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.ArgumentConverters._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import gg.uhc.website.schema.types.scalars.InstantScalarType._
import gg.uhc.website.schema.types.objects.RelayDefinitions.{nodeField, nodesField}
import sangria.schema._

object QueryObjectType extends HasObjectType[Unit] {
  private val usernameArg = Argument(name = "username", argumentType = StringType, description = "Username to match")
  private val usernamesArg = Argument(
    name = "usernames",
    argumentType = ListInputType(StringType),
    description = "List of usernames to match"
  )

  override val Type: ObjectType[SchemaContext, Unit] = ObjectType[SchemaContext, Unit](
    name = "Query",
    description = "Root query object",
    fields = fields[SchemaContext, Unit](
      listingField[Ban, UUID]( // TODO this should be sorted by created
        name = "bans",
        targetType = BanObjectType.Type,
        description = "List of all bans",
        action = _.bans.listing,
        cursorFn = (b: Ban) ⇒ b.uuid
      ),
      listingField[Match, UUID](
        name = "matches",
        targetType = MatchObjectType.Type,
        description = "List of all matches",
        action = _.matches.listing,
        cursorFn = (m: Match) ⇒ m.uuid
      ),
      listingField[Match, Instant](
        name = "upcomingMatches",
        targetType = MatchObjectType.Type,
        description = "List of upcoming matches, sorted by opening first",
        action = ctx ⇒ params ⇒ ctx.matches.getUpcomingMatches(params.withRelId(false)), // query for non-deleted
        cursorFn = (m: Match) ⇒ m.starts
      ),
      listingField[Match, Instant](
        name = "deletedUpcomingMatches",
        targetType = MatchObjectType.Type,
        description = "List of upcoming matches that have been deleted, sorted by opening first",
        action = ctx ⇒ params ⇒ ctx.matches.getUpcomingMatches(params.withRelId(true)), // query for deleted
        cursorFn = (m: Match) ⇒ m.starts
      ),
      listingField[Network, UUID](
        name = "networks",
        targetType = NetworkObjectType.Type,
        description = "List of all networks",
        action = _.networks.listing,
        cursorFn = (n: Network) ⇒ n.uuid
      ),
      listingField[Region, UUID]( // TODO no need to listing this
        name = "regions",
        targetType = RegionObjectType.Type,
        description = "List of all regions",
        action = _.regions.listing,
        cursorFn = (r: Region) ⇒ r.uuid
      ),
      listingField[Role, UUID]( // TODO no need to listing this
        name = "roles",
        targetType = RoleObjectType.Type,
        description = "List of all roles",
        action = _.roles.listing,
        cursorFn = (r: Role) ⇒ r.uuid
      ),
      listingField[Scenario, UUID](
        name = "scenarios",
        targetType = ScenarioObjectType.Type,
        description = "List of all scenarios",
        action = _.scenarios.listing,
        cursorFn = (s: Scenario) ⇒ s.uuid
      ),
      listingField[Style, UUID]( // TODO no need to listing this
        name = "styles",
        targetType = StyleObjectType.Type,
        description = "List of all styles",
        action = _.styles.listing,
        cursorFn = (s: Style) ⇒ s.uuid
      ),
      listingField[Server, UUID](
        name = "servers",
        targetType = ServerObjectType.Type,
        description = "List of all servers",
        action = _.servers.listing,
        cursorFn = (s: Server) ⇒ s.uuid
      ),
      listingField[Version, UUID](
        name = "versions",
        targetType = VersionObjectType.Type,
        description = "List of all versions",
        action = _.versions.listing,
        cursorFn = (v: Version) ⇒ v.uuid
      ),
      Field(
        name = "complexity",
        fieldType = FloatType,
        description = Some("How complex the current query is"),
        complexity = Some((_, _, _) ⇒ 0),
        resolve = ctx ⇒ ctx.ctx.metadata.complexity.get // must exist if this is being resovled
      ),
      Field(
        name = "depth",
        fieldType = IntType,
        description = Some("How nested the current query is"),
        complexity = Some((_, _, _) ⇒ 0),
        resolve = ctx ⇒ ctx.ctx.metadata.depth.get // must exist if this is being resovled
      ),
      nodeField,
      nodesField,
      Field(
        name = "userByUsername",
        fieldType = OptionType(UserObjectType.Type),
        arguments = usernameArg :: Nil,
        resolve = implicit ctx ⇒ ctx.ctx.users.getByUsername(usernameArg.resolve),
        description = Some("Fetch a user with the given username")
      ),
      Field(
        name = "usersByUsernames",
        fieldType = ListType(UserObjectType.Type),
        arguments = usernamesArg :: Nil,
        complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(usernamesArg).length * childScore)),
        resolve = implicit ctx ⇒ ctx.ctx.users.getByUsernames(usernamesArg.resolve),
        description = Some("Fetch users with the given usernames")
      )
    )
  )
}
