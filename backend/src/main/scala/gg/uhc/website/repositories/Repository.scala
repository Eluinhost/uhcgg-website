package gg.uhc.website.repositories

import doobie.imports._
import scoobie.ast._
import scoobie.doobie.ScoobieFragmentProducer
import scoobie.doobie.doo.postgres._
import scoobie.snacks.mild.sql._

import scalaz._
import Scalaz._

trait Repository[A] {
  private[repositories] implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  private[repositories] implicit class QueryBuilderExtensions[F[_]](qb: QueryBuilder[F]) {

    /**
      * Same as regular `where` but accepts an optional comparison
      *
      * If the comparison does not exist the where clause doesn't apply,
      * otherwise the comparison + where clause is added
      */
    def where(queryComparison: Option[QueryComparison[F]]): QueryBuilder[F] =
      queryComparison.map(qb.where).getOrElse(qb)
  }

  private[repositories] implicit class QueryValueExtensions[F[_]](val a: QueryPath[F]) {

    /**
      * Same as regular `in` method but accepts a NonEmptyList instead
      */
    def in(values: NonEmptyList[QueryValue[F]]): QueryComparison[F] =
      QueryIn(a, values.toList)

    /**
      * Same as `in` but accepts a list of values in a NonEmptyList
      */
    def in[V](values: NonEmptyList[V])(implicit ev: F[V]): QueryComparison[F] =
      QueryIn(a, values.map(QueryParameter[F, V]).toList)
  }

  private[repositories] implicit class QueryComparisonExtensions[F[_]](val left: QueryComparison[F]) {

    /**
      * Same as a regular `and` but accepts an optional right comparison.
      *
      * If the right comparison does not exits no AND clause is added.
      */
    def and(right: Option[QueryComparison[F]]): QueryComparison[F] =
      right.map(QueryAnd(left, _)).getOrElse(left)

    /**
      * Same as a regular `or` but accepts an optional right comparison.
      *
      * If the right comparison does not exits no OR clause is added.
      */
    def or(right: Option[QueryComparison[F]]): QueryComparison[F] =
      right.map(QueryOr(left, _)).getOrElse(left)
  }

  private[repositories] implicit class QueryPathExtensions[F[_]](val path: QueryPath[F]) {

    /**
      * As an alternative to .asc and .desc accepting a SortDirection to do automatic conversion
      */
    def sort(direction: SortDirection): QuerySort[F] =
      direction match {
        case SortDirection.ASC  ⇒ path.asc
        case SortDirection.DESC ⇒ path.desc
      }
  }

  private[repositories] implicit class QuerySortExtensions[F[_]](val sort: QuerySort[F]) {

    /**
      * QuerySort has 2 implementations both with 'path' but not defined in the trait so this method makes it
      * available via the interface
      */
    def path: QueryPath[F] = sort match {
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
