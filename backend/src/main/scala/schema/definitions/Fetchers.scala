package schema.definitions

import java.util.UUID

import sangria.execution.deferred.{Fetcher, HasId, RelationIds}
import schema.SchemaContext
import schema.model.Ban

object Fetchers {
  val users = Fetcher.caching(
    (ctx: SchemaContext, ids: Seq[UUID]) ⇒ ctx.users.getByIds(ids)
  )(HasId(_.id))

  val bans = Fetcher.relCaching(
    (ctx: SchemaContext, ids: Seq[Long]) ⇒ ctx.bans.getBansByIds(ids),
    (ctx: SchemaContext, ids: RelationIds[Ban]) ⇒ ctx.bans.getByRelations(ids)
  )(HasId(_.id))
}
