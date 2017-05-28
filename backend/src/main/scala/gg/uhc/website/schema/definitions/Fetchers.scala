package gg.uhc.website.schema.definitions

import java.util.UUID

import gg.uhc.website.repositories._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.model._
import sangria.execution.deferred._

object Fetchers {
  private def simpleFetcher[ID, A <: IdentificationFields[ID], R <: CanQueryByIds[ID, A]](
      repo: SchemaContext ⇒ R
    )(implicit id: HasId[A, ID]
    ): Fetcher[SchemaContext, A, A, ID] =
    Fetcher.caching(
      fetch = (ctx: SchemaContext, ids: Seq[ID]) ⇒ ctx run repo(ctx).getByIds(ids)
    )

  private def relFetcher[ID, A <: IdentificationFields[ID], R <: CanQueryByIds[ID, A] with CanQueryByRelations[A]](
      repo: SchemaContext ⇒ R
    )(implicit id: HasId[A, ID]
    ): Fetcher[SchemaContext, A, A, ID] =
    Fetcher.relCaching(
      fetch = (ctx: SchemaContext, ids: Seq[ID]) ⇒ ctx run repo(ctx).getByIds(ids),
      fetchRel = (ctx: SchemaContext, ids: RelationIds[A]) ⇒ ctx run repo(ctx).getByRelations(ids)
    )

  private def relOnlyFetcher[A, R <: CanQueryByRelations[A]](
      repo: SchemaContext ⇒ R
    ): Fetcher[SchemaContext, A, A, _] =
    Fetcher.relCaching(
      fetch = (ctx: SchemaContext, ids: Seq[_]) ⇒ ???,
      fetchRel = (ctx: SchemaContext, ids: RelationIds[A]) ⇒ ctx run repo(ctx).getByRelations(ids)
    )(HasId(identity))

  val users    = simpleFetcher[UUID, User, UserRepository](_.users)
  val regions  = simpleFetcher[Int, Region, RegionRepository](_.regions)
  val versions = simpleFetcher[Int, Version, VersionRepository](_.versions)
  val styles   = simpleFetcher[Int, Style, StyleRepository](_.styles)
  val roles    = simpleFetcher[Int, Role, RoleRepository](_.roles)

  val bans      = relFetcher[Long, Ban, BanRepository](_.bans)
  val networks  = relFetcher[Long, Network, NetworkRepository](_.networks)
  val servers   = relFetcher[Long, Server, ServerRepository](_.servers)
  val matches   = relFetcher[Long, Match, MatchRepository](_.matches)
  val scenarios = relFetcher[Long, Scenario, ScenarioRepository](_.scenarios)

  val userRoles          = relOnlyFetcher[UserRole, UserRolesRepository](_.userRoles)
  val matchScenarios     = relOnlyFetcher[MatchScenario, MatchScenariosRepository](_.matchScenarios)
  val networkPermissions = relOnlyFetcher[NetworkPermission, NetworkPermissionRepository](_.networkPermissions)

  //  val bans = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx run ctx.bans.getByIds(ids),
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[Ban]) ⇒ ctx.bans.getByRelations(ids)
//  )
//
//  val roles = nonRelationFetcher[Role, Int, RoleRepository](_.roles)
//
//  val userRoles = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[UserRole]) ⇒ Future failed ???,
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[UserRole]) ⇒ ctx.userRoles.getByRelations(ids)
//  )
//
//  val networks = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.networks.getByIds(ids),
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[Network]) ⇒ ctx.networks.getByRelations(ids)
//  )
//
//  val servers = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ Future failed ???,
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[Server]) ⇒ ctx.servers.getByRelations(ids)
//  )
//
//  val matches = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.matches.getByIds(ids),
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[Match]) ⇒ ctx.matches.getByRelations(ids)
//  )
//
//  val scenarios = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.scenarios.getByIds(ids),
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[Scenario]) ⇒ ctx.scenarios.getByRelations(ids)
//  )
//
//  val matchScenarios = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[MatchScenario]) ⇒ Future failed ???,
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[MatchScenario]) ⇒ ctx.matchScenarios.getByRelations(ids)
//  )(HasId(identity))
//
//  val networkPermissions = Fetcher.relCaching(
//    fetch = (ctx: SchemaContext, ids: Seq[NetworkPermission]) ⇒ Future failed ???,
//    fetchRel = (ctx: SchemaContext, ids: RelationIds[NetworkPermission]) ⇒ ctx.networkPermissions.getByRelations(ids)
//  )(HasId(identity))

  val fetchers
    : List[Fetcher[SchemaContext, _, _, _]] = users :: bans :: roles :: userRoles :: regions :: versions :: networks :: servers :: styles :: matches :: scenarios :: matchScenarios :: networkPermissions :: Nil
}
