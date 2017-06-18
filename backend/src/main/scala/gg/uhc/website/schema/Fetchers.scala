package gg.uhc.website.schema

import java.util.UUID

import gg.uhc.website.model._
import gg.uhc.website.repositories._
import sangria.execution.deferred._

object Fetchers {
  private def simpleFetcher[A <: BaseNode, R <: HasUuidIdColumn[A]](
      repo: SchemaContext ⇒ R
    ): Fetcher[SchemaContext, A, A, UUID] =
    Fetcher.caching(
      fetch = (ctx: SchemaContext, ids: Seq[UUID]) ⇒ ctx run repo(ctx).getByIds(ids)
    )(HasId(_.uuid))

  val users     = simpleFetcher[User, UserRepository](_.users)
  val regions   = simpleFetcher[Region, RegionRepository](_.regions)
  val versions  = simpleFetcher[Version, VersionRepository](_.versions)
  val styles    = simpleFetcher[Style, StyleRepository](_.styles)
  val roles     = simpleFetcher[Role, RoleRepository](_.roles)
  val bans      = simpleFetcher[Ban, BanRepository](_.bans)
  val networks  = simpleFetcher[Network, NetworkRepository](_.networks)
  val servers   = simpleFetcher[Server, ServerRepository](_.servers)
  val matches   = simpleFetcher[Match, MatchRepository](_.matches)
  val scenarios = simpleFetcher[Scenario, ScenarioRepository](_.scenarios)

  val fetchers: List[Fetcher[SchemaContext, _, _, _]] =
    users ::
      bans ::
      roles ::
      regions ::
      versions ::
      networks ::
      servers ::
      styles ::
      matches ::
      scenarios ::
      Nil
}
