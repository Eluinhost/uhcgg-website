package schema.definitions

import java.util.UUID

import sangria.execution.deferred._
import schema.SchemaContext
import schema.model.{Ban, UserRole}

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

  val fetchers = users :: bans :: roles :: userRoles :: regions :: versions :: Nil
}
