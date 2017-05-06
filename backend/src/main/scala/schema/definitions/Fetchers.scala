package schema.definitions

import java.util.UUID

import sangria.execution.deferred._
import schema.SchemaContext
import schema.model.Ban

object Fetchers {
  val users = Fetcher.caching(
    fetch = (ctx: SchemaContext, ids: Seq[UUID]) ⇒ ctx.users.getByIds(ids)
  )(HasId(_.id))

  val bans = Fetcher.relCaching(
    fetch = (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.bans.getBansByIds(ids),
    fetchRel = (ctx: SchemaContext, ids: RelationIds[Ban]) ⇒ ctx.bans.getByRelations(ids)
  )(HasId(_.id))
}
