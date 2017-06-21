package gg.uhc.website.repositories

import doobie.imports._
import scoobie.ast._
import scoobie.doobie.doo.postgres._
import scoobie.doobie.ScoobieFragmentProducer
import scoobie.snacks.mild.sql._

trait HasRelationColumns[A] { self: Repository[A] ⇒
  private[repositories] implicit val logHandler: LogHandler

  /**
    * Creates a Query that will look up items in this table that match the given relation.
    *
    * e.g. look up all servers that belong to the given network
    * ->
    * `SELECT columns FROM servers WHERE network_id = 'something' AND uuid > 'cursor' LIMIT 50 ORDER BY uuid`
    *
    * @param relColumn the column the relation is defined in
    * @param sort the cursor column + direction
    * @param query the listing parameters to filter output data
    * @tparam RelId the type of the relation column
    * @tparam Cursor the type of the cursor column
    */
  private[repositories] def relationListingQuery[RelId, Cursor](
      relColumn: QueryPath[ScoobieFragmentProducer],
      sort: QuerySort[ScoobieFragmentProducer]
    )(query: RelationshipListingParameters[RelId, Cursor]
    )(implicit
      relFragmentProducer: ScoobieFragmentProducer[RelId],
      cursorFragmentProducer: ScoobieFragmentProducer[Cursor]
    ) = {
    val relFilter    = relColumn === query.relId
    val cursorFilter = query.after.map(cursor ⇒ sort.path > cursor)

    (baseSelect where (relFilter and cursorFilter) orderBy sort limit query.count).build.query[A]
  }
}
