package schema.definitions

import java.util.UUID

import sangria.execution.deferred._
import schema.SchemaContext
import schema.model._

import scala.concurrent.Future

object Fetchers {
  val users = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[UUID]) ⇒ ctx.users.getByIds(ids)
  )(HasId(_.id))

  val bans = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.bans.getBansByIds(ids),
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Ban]) ⇒ ctx.bans.getByRelations(ids)
  )(HasId(_.id))

  val roles = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[Int]) ⇒ ctx.roles.getByIds(ids)
  )(HasId(_.id))

  val userRoles = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[UserRole]) ⇒ Future failed ???,
    fetchRel = (ctx: SchemaContext, ids: RelationIds[UserRole]) ⇒ ctx.userRoles.getByRelations(ids)
  )(HasId(it ⇒ it))

  val regions = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[Int]) ⇒ ctx.regions.getByIds(ids)
  )(HasId(_.id))

  val versions = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[Int]) ⇒ ctx.versions.getByIds(ids)
  )(HasId(_.id))

  val networks = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.networks.getByIds(ids),
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Network]) ⇒ ctx.networks.getByRelations(ids)
  )(HasId(_.id))

  val servers = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ Future failed ???,
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Server]) ⇒ ctx.servers.getByRelations(ids)
  )(HasId(_.id))

  val styles = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[Int]) ⇒ ctx.styles.getByIds(ids)
  )(HasId(_.id))

  val matches = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.matches.getByIds(ids),
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Match]) ⇒ ctx.matches.getByRelations(ids)
  )(HasId(_.id))

  val scenarios = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.scenarios.getByIds(ids),
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Scenario]) ⇒ ctx.scenarios.getByRelations(ids)
  )(HasId(_.id))

  val fetchers = users :: bans :: roles :: userRoles :: regions :: versions :: networks :: servers :: styles :: matches :: scenarios :: Nil
}
