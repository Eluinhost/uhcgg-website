package gg.uhc.website.repositories

import java.util.UUID

import doobie.free.connection
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.repositories.HasRelationColumns.{RelationshipLookup, RelationshipQuery}

import scalaz.NonEmptyList

object SortDirection {
  case object ASC extends SortDirection {
    override val sql: String = "ASC"
  }
  case object DESC extends SortDirection {
    override val sql: String = "DESC"
  }
}

sealed trait SortDirection {
  val sql: String
}

trait Repository[A] {
  private[repositories] implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  /**
    * Implement to define the base SQL select query. e.g. `SELECT a, b, c, d FROM items`
    */
  private[repositories] val select: Fragment

  /**
    * This is used to convert row responses back into domain objects.
    *
    * Simple implementation can be done like `override val composite: Composite[A] = implicitly`
    */
  private[repositories] implicit val composite: Composite[A]

  /**
    * Simple alias for Fragment.const
    */
  private[repositories] def const(raw: String): Fragment = Fragment.const(raw)

  /**
    * Simple alias for Fragment.empty
    */
  private[repositories] val empty: Fragment = Fragment.empty

  /**
    * Generates an SQL LIMIT clause
    */
  private[repositories] def limitTo(limit: Long): Fragment =
    fr"LIMIT $limit".asInstanceOf[Fragment]

  /**
    * Filters result set to only include items after the given cursor.
    *
    * Makes SQL like `column > 'value'`
    *
    * This fragment won't make much sense unless an ORDER BY is applied to the cursor column also
    *
    * @param cursorColumn the column to apply the cursor to MUST BE SAFE TO USE IN A SQL STRING
    * @param cursor the cursor item to filter on, shows items AFTER this item
    * @tparam Cursor the type of the cursor column/item
    */
  private[repositories] def cursorFilter[Cursor: Param](cursorColumn: String, cursor: Cursor): Fragment =
    const(s"$cursorColumn > ") ++ fr"$cursor".asInstanceOf[Fragment]

  // Adds an order by for the given cursor column + direction
  /**
    * Orders by the specified column + direction.
    *
    * Makes SQL like `ORDER BY column ASC`
    *
    * @param column the name of the column to sort on MUST BE SAFE TO USE IN AN SQL STRING
    * @param direction which way to sort the column
    */
  private[repositories] def orderBy(column: String, direction: SortDirection) =
    const(s"ORDER BY $column ${direction.sql} ")

  /**
    * Filter where a specific column is equal to the provided value
    *
    * @param column the name of the column to check. MUST BE SAFE SQL
    * @param v the value to filter by
    */
  private[repositories] def columnEqFilter[V: Param](column: String, v: V): Fragment =
    const(s"$column = ") ++ fr"$v".asInstanceOf[Fragment]

  private[repositories] def getAllQuery: Query0[A] =
    select.query[A]

  def getAll: ConnectionIO[List[A]] = getAllQuery.list
}

trait HasUuidIdColumn[A] extends HasIdColumn[A, UUID] { self: Repository[A] ⇒
  override private[repositories] val idColumn: String     = "uuid"
  override private[repositories] val idParam: Param[UUID] = implicitly[Param[UUID]]
}

trait HasIdColumn[A, Id] { self: Repository[A] ⇒
  private[repositories] implicit val logHandler: LogHandler

  /**
    * This is used to convert row responses back into domain objects.
    *
    * Simple implementation can be done like `override val idParam: Param[Id] = implicitly`
    */
  private[repositories] implicit val idParam: Param[Id]

  /**
    * Name of the column that holds the ID for this type
    */
  private[repositories] val idColumn: String

  /**
    * Filter for finding by a specific ID
    * @param id the ID to lookup
    */
  private[repositories] def idFilter(id: Id): Fragment =
    columnEqFilter(idColumn, id)

  /**
    * Query to find by the specific ID
    * @param id the ID to lookup
    */
  private[repositories] def getByIdQuery(id: Id): Query0[A] =
    (
      select ++
        Fragments.whereAnd(
          idFilter(id)
        )
    ).query[A]

  /**
    * Query to find any matching the specified IDs
    * @param ids the IDs to lookup
    */
  private[repositories] def getByIdsQuery(ids: NonEmptyList[Id]): Query0[A] =
    (
      select ++
        Fragments.whereAnd(
          Fragments.in(Fragment.const(s"$idColumn "), ids)
        )
    ).query[A]

  /**
    * Query to look up a 'listing' for items, cursor is the ID column
    *
    * @param limit limit to x amount of items
    * @param after only show items after this ID
    */
  private[repositories] def listingQuery(after: Option[Id], limit: Long) =
    (
      select ++
        Fragments.whereAndOpt(
          after.map(cursorFilter(idColumn, _))
        ) ++
        limitTo(limit) ++
        orderBy(column = "uuid", SortDirection.ASC)
    ).query[A]

  /**
    * Look up a listing of all items sorted by their ID
    *
    * @param limit limit to x number of items
    * @param after only show items after this ID
    */
  def listing(after: Option[Id], limit: Long): ConnectionIO[List[A]] = listingQuery(after, limit).list

  /**
    * Lookup an item by it's ID
    *
    * @param id the ID to lookup
    * @return a ConnectionIO containing the item if it exists
    */
  def getById(id: Id): ConnectionIO[Option[A]] =
    getByIdQuery(id).option

  /**
    * Lookup items from the given IDs
    *
    * @param ids the IDs to lookup
    * @return a ConnectionIO[List] containing the matched items
    */
  def getByIds(ids: Seq[Id]): ConnectionIO[List[A]] =
    ids match {
      // if at least one item in IDs run the query
      case a +: as ⇒ getByIdsQuery(NonEmptyList(a, as: _*)).list
      // otherwise don't run anything and use an empty list instead
      case _ ⇒ connection.raw(_ ⇒ List.empty[A])
    }
}

object HasRelationColumns {
  type RelationshipQuery[Row, RelId, Cursor]  = (RelId, Option[Cursor], Long) ⇒ Query0[Row]
  type RelationshipLookup[Row, RelId, Cursor] = (RelId, Option[Cursor], Long) ⇒ ConnectionIO[List[Row]]
}

trait HasRelationColumns[A] { self: Repository[A] ⇒
  type QueryA[RelId, Cursor]  = RelationshipQuery[A, RelId, Cursor]
  type LookupA[RelId, Cursor] = RelationshipLookup[A, RelId, Cursor]

  private[repositories] implicit val logHandler: LogHandler

  /**
    * At its basic converts a Query0 to ConnectionIO by calling list on it
    */
  private[repositories] implicit def queryToLookup[RelId, Cursor](
      query: QueryA[RelId, Cursor]
    ): LookupA[RelId, Cursor] =
    (relId: RelId, cursor: Option[Cursor], limit: Long) ⇒ query(relId, cursor, limit).list

  /**
    * Creates a Query that will look up items in this table that match the given relation.
    *
    * e.g. look up all servers that belong to the given network
    * ->
    * `SELECT columns FROM servers WHERE networkId = 'something' AND uuid > 'cursor' LIMIT 50 ORDER BY uuid`
    *
    * @param relColumn the name of the column the relation is defined in. MUST BE SAFE SQL
    * @param cursorColumn the name of the column the cursor is defined in. MUST BE SAFE SQL
    * @param cursorDirection the direction to order the cursor by
    * @tparam RelId the type of the relation column
    * @tparam Cursor the type of the cursor column
    */
  private[repositories] def relationListingQuery[RelId, Cursor](
      relColumn: String,
      cursorColumn: String,
      cursorDirection: SortDirection = SortDirection.ASC
    )(implicit relParam: Param[RelId],
      cursorParam: Param[Cursor]
    ): QueryA[RelId, Cursor] = {

    // Filters the result set to only things matching the relationship key
    def relColumnFilter(relId: RelId): Fragment =
      const(s"$relColumn = ") ++ fr"$relId".asInstanceOf[Fragment]

    def createQuery(relId: RelId, cursor: Option[Cursor], limit: Long): Query0[A] = {
      (
        select ++
          Fragments.whereAndOpt(
            Some(relColumnFilter(relId)), // always filter by the rel column
            cursor.map(cursorFilter(cursorColumn, _)) // optionally add the filter for the cursor value
          ) ++
          // Add an order by to make after cursors work + always add a limit
          orderBy(cursorColumn, cursorDirection) ++
          limitTo(limit)
      ).query[A]
    }

    createQuery
  }
}
