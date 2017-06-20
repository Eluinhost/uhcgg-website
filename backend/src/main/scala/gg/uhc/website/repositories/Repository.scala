package gg.uhc.website.repositories

import java.util.UUID

import doobie.free.connection
import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.repositories.HasRelationColumns.{RelationshipLookup, RelationshipQuery}
import scoobie.ast._

import scalaz._
import Scalaz._

object SortDirection {
  case object ASC  extends SortDirection
  case object DESC extends SortDirection
}

sealed trait SortDirection

import scoobie.doobie.ScoobieFragmentProducer
import scoobie.doobie.doo.postgres._
import scoobie.snacks.mild.sql._

trait Repository[A] {
  private[repositories] implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  private[repositories] implicit class QueryBuilderExtensions[F[_]](qb: QueryBuilder[F]) {
    def where(queryComparison: Option[QueryComparison[F]]): QueryBuilder[F] =
      queryComparison match {
        case None ⇒
          qb
        case Some(comp) ⇒
          qb where comp
      }
  }

  private[repositories] implicit class QueryValueExtensions[F[_]](val a: QueryPath[F]) {
    def in(values: NonEmptyList[QueryValue[F]]): QueryComparison[F] =
      QueryIn(a, values.toList)

    def in[V](values: NonEmptyList[V])(implicit ev: F[V]): QueryComparison[F] =
      QueryIn(a, values.map(QueryParameter[F, V]).toList)
  }

  private[repositories] implicit class QueryComparisonExtensions[F[_]](val left: QueryComparison[F]) {
    def and(right: Option[QueryComparison[F]]): QueryComparison[F] =
      right.map(QueryAnd(left, _)).getOrElse(left)

    def or(right: Option[QueryComparison[F]]): QueryComparison[F] =
      right.map(QueryOr(left, _)).getOrElse(left)
  }

  private[repositories] implicit class QueryPathExtensions[F[_]](val f: QueryPath[F]) {
    def sort(direction: SortDirection): QuerySort[F] =
      direction match {
        case SortDirection.ASC ⇒
          QuerySortAsc(f)
        case SortDirection.DESC ⇒
          QuerySortDesc(f)
      }
  }

  private[repositories] implicit class QuerySortExtensions[F[_]](val f: QuerySort[F]) {
    def path: QueryPath[F] = f match {
      case QuerySortAsc(path)  ⇒ path
      case QuerySortDesc(path) ⇒ path
      case _                   ⇒ throw new NotImplementedError()
    }
  }

  private[repositories] val baseSelect: QueryBuilder[ScoobieFragmentProducer]

  /**
    * This is used to convert row responses back into domain objects.
    *
    * Simple implementation can be done like `override val composite: Composite[A] = implicitly`
    */
  private[repositories] implicit val composite: Composite[A]

  private[repositories] def getAllQuery: Query0[A] =
    baseSelect.build.query[A]

  def getAll: ConnectionIO[List[A]] = getAllQuery.list
}

trait HasUuidIdColumn[A] extends HasIdColumn[A, UUID] { self: Repository[A] ⇒
  override private[repositories] val idFragmentProducer: ScoobieFragmentProducer[UUID] =
    implicitly[ScoobieFragmentProducer[UUID]]
}

trait HasIdColumn[A, Id] { self: Repository[A] ⇒
  private[repositories] implicit val logHandler: LogHandler

  private[repositories] implicit val idFragmentProducer: ScoobieFragmentProducer[Id]

  /**
    * Path of the column that holds the ID for this type
    */
  private[repositories] val idColumn: QueryPath[ScoobieFragmentProducer]

  /**
    * Query to find by the specific ID
    * @param id the ID to lookup
    */
  private[repositories] def getByIdQuery(id: Id): Query0[A] =
    (baseSelect where (idColumn === QueryParameter[ScoobieFragmentProducer, Id](id))).build.query[A]

  /**
    * Query to find any matching the specified IDs
    * @param ids the IDs to lookup
    */
  private[repositories] def getByIdsQuery(ids: NonEmptyList[Id]): Query0[A] =
    (baseSelect where (idColumn in ids)).build.query[A]

  /**
    * Query to look up a 'listing' for items, cursor is the ID column
    *
    * @param limit limit to x amount of items
    * @param after only show items after this ID
    */
  private[repositories] def listingQuery(after: Option[Id], limit: Int): Query0[A] =
    (baseSelect where after.map(id ⇒ idColumn > QueryParameter[ScoobieFragmentProducer, Id](id)) orderBy idColumn.asc limit limit).build
      .query[A]

  /**
    * Look up a listing of all items sorted by their ID
    *
    * @param limit limit to x number of items
    * @param after only show items after this ID
    */
  def listing(after: Option[Id], limit: Int): ConnectionIO[List[A]] = listingQuery(after, limit).list

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
  type RelationshipQuery[Row, RelId, Cursor]  = (RelId, Option[Cursor], Int) ⇒ Query0[Row]
  type RelationshipLookup[Row, RelId, Cursor] = (RelId, Option[Cursor], Int) ⇒ ConnectionIO[List[Row]]
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
    (relId: RelId, cursor: Option[Cursor], limit: Int) ⇒ query(relId, cursor, limit).list

  /**
    * Creates a Query that will look up items in this table that match the given relation.
    *
    * e.g. look up all servers that belong to the given network
    * ->
    * `SELECT columns FROM servers WHERE network_id = 'something' AND uuid > 'cursor' LIMIT 50 ORDER BY uuid`
    *
    * @param relColumn the name of the column the relation is defined in
    * @param sort the cursor column + direction
    * @tparam RelId the type of the relation column
    * @tparam Cursor the type of the cursor column
    */
  private[repositories] def relationListingQuery[RelId, Cursor](
      relColumn: QueryPath[ScoobieFragmentProducer],
      sort: QuerySort[ScoobieFragmentProducer]
    )(implicit
      relFragmentProducer: ScoobieFragmentProducer[RelId],
      cursorFragmentProducer: ScoobieFragmentProducer[Cursor]
    ): QueryA[RelId, Cursor] = {

    def createQuery(relId: RelId, cursor: Option[Cursor], limit: Int): Query0[A] = {
      val relFilter    = relColumn === QueryParameter[ScoobieFragmentProducer, RelId](relId)
      val cursorFilter = cursor.map(cursor ⇒ sort.path > QueryParameter[ScoobieFragmentProducer, Cursor](cursor))

      (baseSelect where (relFilter and cursorFilter) orderBy sort limit limit).build.query[A]
    }

    createQuery
  }
}
