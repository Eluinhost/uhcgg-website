package gg.uhc.website.schema.helpers

import doobie.imports.ConnectionIO
import gg.uhc.website.repositories.HasRelationColumns.RelationshipLookup
import gg.uhc.website.schema.{ConnectionArguments, SchemaContext}
import sangria.relay.{Connection, DefaultConnection, Edge, PageInfo}
import sangria.schema.{Args, Context, Field, ObjectType, ScalarType}

import scala.concurrent.Future

object ConnectionHelpers extends ConnectionHelpers

trait ConnectionHelpers {
  def listingField[Target, Cursor: ScalarType](
      name: String,
      targetType: ObjectType[SchemaContext, Target],
      description: String,
      action: SchemaContext ⇒ (Option[Cursor], Int) ⇒ ConnectionIO[List[Target]],
      cursorFn: (Target ⇒ Cursor)
    ): Field[SchemaContext, Unit] =
    Field(
      name = name,
      fieldType = Connection
        .definition[SchemaContext, Connection, Target](targetType.name, targetType)
        .connectionType,
      arguments = ConnectionArguments.All[Cursor],
      complexity = Some(
        (_: SchemaContext, args: Args, childScore: Double) ⇒ 25 + (args.arg(ConnectionArguments.First) * childScore)
      ),
      description = Some(description),
      resolve = ctx ⇒ resolveListing[Target, Cursor](action, cursorFn)(ctx)
    )

  def resolveListing[Target, Cursor: ScalarType](
      action: SchemaContext ⇒ (Option[Cursor], Int) ⇒ ConnectionIO[List[Target]],
      cursorFn: Target ⇒ Cursor
    )(ctx: Context[SchemaContext, Unit]
    ): Future[Connection[Target]] = {
    // Use DB execution context
    import ctx.ctx.run.ec

    val ConnectionArguments(limit, cursor) = ConnectionArguments[Cursor](ctx)

    // We request 1 extra item to check if there is a next page
    val toRun: ConnectionIO[List[Target]] = action(ctx.ctx)(cursor, limit + 1)

    ctx.ctx
      .run(toRun)
      .map(data ⇒ generateConnection(data = data.take(limit), cursorFn = cursorFn, hasMore = data.size > limit))
  }

  def relationshipField[A, Target, RelId, Cursor: ScalarType](
      name: String,
      targetType: ObjectType[SchemaContext, Target],
      description: String,
      action: SchemaContext ⇒ RelationshipLookup[Target, RelId, Cursor],
      cursorFn: (Target ⇒ Cursor), // TODO figure out how to move this to the repo query so it can be safer to use
      idFn: (A ⇒ RelId)
    ): Field[SchemaContext, A] =
    Field(
      name = name,
      fieldType = Connection
        .definition[SchemaContext, Connection, Target](targetType.name, targetType)
        .connectionType,
      arguments = ConnectionArguments.All[Cursor],
      complexity = Some(
        (_: SchemaContext, args: Args, childScore: Double) ⇒ 25 + (args.arg(ConnectionArguments.First) * childScore)
      ),
      description = Some(description),
      resolve = ctx ⇒ resolveRelationship[A, Target, RelId, Cursor](action, cursorFn, idFn)(ctx)
    )

  def resolveRelationship[A, Target, RelId, Cursor: ScalarType](
      action: SchemaContext ⇒ RelationshipLookup[Target, RelId, Cursor],
      cursorFn: (Target ⇒ Cursor),
      idFn: (A ⇒ RelId)
    )(ctx: Context[SchemaContext, A]
    ): Future[Connection[Target]] = {
    // Use DB execution context
    import ctx.ctx.run.ec

    val id: RelId                          = idFn(ctx.value)
    val ConnectionArguments(limit, cursor) = ConnectionArguments[Cursor](ctx)

    // We request 1 extra item to check if there is a next page
    val toRun: ConnectionIO[List[Target]] = action(ctx.ctx)(id, cursor, limit + 1)

    ctx.ctx
      .run(toRun)
      .map(data ⇒ generateConnection(data = data.take(limit), cursorFn = cursorFn, hasMore = data.size > limit))
  }

  private def generateConnection[Target, Cursor](
      data: List[Target],
      cursorFn: Target ⇒ Cursor,
      hasMore: Boolean
    ): Connection[Target] =
    DefaultConnection(
      PageInfo(
        startCursor = data.headOption.map(cursorFn).map(_.toString),
        endCursor = data.lastOption.map(cursorFn).map(_.toString),
        hasPreviousPage = false,
        hasNextPage = hasMore
      ),
      data.map { row ⇒
        Edge(row, cursorFn(row).toString)
      }
    )
}
