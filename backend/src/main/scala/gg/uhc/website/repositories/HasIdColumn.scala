package gg.uhc.website.repositories

import doobie.free.connection.raw
import doobie.imports._
import scoobie.ast._
import scoobie.doobie.doo.postgres._
import scoobie.doobie.ScoobieFragmentProducer
import scoobie.snacks.mild.sql._

import scalaz.NonEmptyList

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
    (baseSelect where (idColumn === id)).build.query[A]

  /**
    * Query to find any matching the specified IDs
    * @param ids the IDs to lookup
    */
  private[repositories] def getByIdsQuery(ids: NonEmptyList[Id]): Query0[A] =
    (baseSelect where (idColumn in ids)).build.query[A]

  /**
    * Query to look up a 'listing' for items, cursor is the ID column
    */
  private[repositories] def listingQuery(params: ListingParameters[Id]): Query0[A] =
    (baseSelect where params.after.map(id ⇒ idColumn > QueryParameter[ScoobieFragmentProducer, Id](id)) orderBy idColumn.asc limit params.count).build
      .query[A]

  /**
    * Look up a listing of all items sorted by their ID
    */
  def listing(params: ListingParameters[Id]): ConnectionIO[List[A]] = listingQuery(params).list

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
      case _ ⇒ raw(_ ⇒ List.empty[A])
    }
}
