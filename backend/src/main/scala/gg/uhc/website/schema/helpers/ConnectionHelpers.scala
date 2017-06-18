package gg.uhc.website.schema.helpers

import doobie.imports.ConnectionIO
import gg.uhc.website.repositories.CanQueryRelations.RelationshipLookup
import gg.uhc.website.schema.{ConnectionArguments, SchemaContext}
import sangria.relay.{Connection, DefaultConnection, Edge, PageInfo}
import sangria.schema.{Args, Context, Field, ObjectType, ScalarType}

import scala.concurrent.Future

object ConnectionHelpers extends ConnectionHelpers

trait ConnectionHelpers {
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
        (ctx: SchemaContext, args: Args, childScore: Double) ⇒ 25 + (args.arg(ConnectionArguments.First) * childScore)
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

    val toRun: ConnectionIO[List[Target]] = action(ctx.ctx)(id, cursor, limit)

    ctx.ctx.run(toRun).map { data ⇒
      DefaultConnection(
        PageInfo(
          startCursor = data.headOption.map(cursorFn).map(_.toString),
          endCursor = data.lastOption.map(cursorFn).map(_.toString),
          hasPreviousPage = false,
          hasNextPage = false // TODO we need to request n + 1 items from the DB to detect if there is a next page and cut the last item from the response
        ),
        data.map { row ⇒
          Edge(row, cursorFn(row).toString)
        }
      )
    }
  }
}
