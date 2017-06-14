package gg.uhc.website.schema.definitions

import gg.uhc.website.model._
import gg.uhc.website.repositories._
import gg.uhc.website.schema.SchemaContext
import sangria.execution.deferred._
import sangria.relay.Node

object Fetchers {
  private def simpleFetcher[A <: Node, R <: CanQueryByIds[A]](
      repo: SchemaContext ⇒ R
    ): Fetcher[SchemaContext, A, A, String] =
    Fetcher.caching(
      fetch = (ctx: SchemaContext, ids: Seq[String]) ⇒ ctx run repo(ctx).getByIds(ids)
    )(HasId(_.id))

  private def relFetcher[A <: Node, R <: CanQueryByIds[A] with CanQueryByRelations[A]](
      repo: SchemaContext ⇒ R
    ): Fetcher[SchemaContext, A, A, String] =
    Fetcher.relCaching(
      fetch = (ctx: SchemaContext, ids: Seq[String]) ⇒ ctx run repo(ctx).getByIds(ids),
      fetchRel = (ctx: SchemaContext, ids: RelationIds[A]) ⇒ ctx run repo(ctx).getByRelations(ids)
    )(HasId(_.id))

  private def relOnlyFetcher[A, R <: CanQueryByRelations[A]](
      repo: SchemaContext ⇒ R
    ): Fetcher[SchemaContext, A, A, _] =
    Fetcher.relCaching(
      fetch = (ctx: SchemaContext, ids: Seq[_]) ⇒ ???,
      fetchRel = (ctx: SchemaContext, ids: RelationIds[A]) ⇒ ctx run repo(ctx).getByRelations(ids)
    )(HasId(identity))

  val users    = simpleFetcher[User, UserRepository](_.users)
  val regions  = simpleFetcher[Region, RegionRepository](_.regions)
  val versions = simpleFetcher[Version, VersionRepository](_.versions)
  val styles   = simpleFetcher[Style, StyleRepository](_.styles)
  val roles    = simpleFetcher[Role, RoleRepository](_.roles)

  val bans      = relFetcher[Ban, BanRepository](_.bans)
  val networks  = relFetcher[Network, NetworkRepository](_.networks)
  val servers   = relFetcher[Server, ServerRepository](_.servers)
  val matches   = relFetcher[Match, MatchRepository](_.matches)
  val scenarios = relFetcher[Scenario, ScenarioRepository](_.scenarios)

  val userRoles          = relOnlyFetcher[UserRole, UserRolesRepository](_.userRoles)
  val matchScenarios     = relOnlyFetcher[MatchScenario, MatchScenariosRepository](_.matchScenarios)
  val networkPermissions = relOnlyFetcher[NetworkPermission, NetworkPermissionRepository](_.networkPermissions)

  val fetchers: List[Fetcher[SchemaContext, _, _, _]] =
    users ::
      bans ::
      roles ::
      userRoles ::
      regions ::
      versions ::
      networks ::
      servers ::
      styles ::
      matches ::
      scenarios ::
      matchScenarios ::
      networkPermissions ::
      Nil
}
