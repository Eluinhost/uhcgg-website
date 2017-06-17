package gg.uhc.website.schema.helpers

import java.util.UUID

import doobie.imports.ConnectionIO
import gg.uhc.website.model.BaseNode
import gg.uhc.website.schema.{ForwardOnlyConnection, SchemaContext}
import sangria.relay.{Connection, DefaultConnection, Edge, PageInfo}
import sangria.schema.{Args, Context, Field, ObjectType}

import scala.concurrent.Future

object ConnectionHelpers extends ConnectionHelpers

trait ConnectionHelpers {
  /**
    * Creates a connection ObjectType for the supplied schema type
    *
    * @param o the schema type to connect to
    * @param name the name for the connection (${name}Connection)
    */
  def simpleConnectionType[T](
      o: ObjectType[SchemaContext, T],
      name: String
    ): ObjectType[SchemaContext, Connection[T]] =
    Connection
      .definition[SchemaContext, Connection, T](name, o)
      .connectionType

  /**
    * Same as other method except automatically uses the name from the provided objecttype
    */
  def simpleConnectionType[T](o: ObjectType[SchemaContext, T]): ObjectType[SchemaContext, Connection[T]] =
    simpleConnectionType(o, o.name)

  /**
    * Creates a Field for a connection to the provided target object
    *
    * @param name the name of the generated field
    * @param description the description of the generated field
    * @param target the target object type, passed to #simpleConnectionType
    * @param action the action to run to get the linked data, passed to #resolveConnection
    * @param cursorFn a function to generate cursor information for generated data, passed to #resolveConnection
    */
  def simpleConnectionField[A <: BaseNode, T](
      name: String,
      target: ObjectType[SchemaContext, T],
      description: String,
      action: SchemaContext ⇒ (UUID, ForwardOnlyConnection) ⇒ ConnectionIO[List[T]],
      cursorFn: (T ⇒ String)
    ): Field[SchemaContext, A] = Field(
    name = name,
    fieldType = simpleConnectionType(target),
    arguments = ForwardOnlyConnection.Args.All,
    complexity = connectionComplexity,
    description = Some(description),
    resolve = resolveConnection[A, T](action, cursorFn)
  )

  def resolveConnection[A <: BaseNode, B](
      action: SchemaContext ⇒ (UUID, ForwardOnlyConnection) ⇒ ConnectionIO[List[B]],
      cursorFn: (B ⇒ String)
    )(ctx: Context[SchemaContext, A]
    ): Future[Connection[B]] = {
    // Use DB execution context
    import ctx.ctx.run.ec

    val connectionArgs = ForwardOnlyConnection(ctx)
    val toRun          = action(ctx.ctx)(ctx.value.uuid, connectionArgs)

    ctx.ctx.run(toRun).map { data ⇒
      DefaultConnection(
        PageInfo(
          startCursor = data.headOption.map(cursorFn),
          endCursor = data.lastOption.map(cursorFn),
          hasPreviousPage = false,
          hasNextPage = false // TODO we need to request n + 1 items from the DB to detect if there is a next page and cut the last item from the response
        ),
        data.map(row ⇒ Edge(row, cursorFn(row)))
      )
    }
  }

  /**
    * Generic connection complexity. Starts with a base of 25 and then multiplies the child score by the amount of
    * requested items
    */
  val connectionComplexity: Option[(SchemaContext, Args, Double) ⇒ Double] = Some(
    (ctx: SchemaContext, args: Args, childScore: Double) ⇒
      25 + (args.arg(ForwardOnlyConnection.Args.First) * childScore)
  )
}
