package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.model.BaseNode
import gg.uhc.website.repositories.CanQueryRelations.{RelationshipLookup, RelationshipQuery}

import scalaz.Scalaz._
import scalaz._

trait Repository[A] {
  sealed trait SortDirection {
    val sql: String
  }
  case object ASC extends SortDirection {
    override val sql: String = "ASC"
  }
  case object DESC extends SortDirection {
    override val sql: String = "DESC"
  }

  private[repositories] implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  // Used to query for the data
  private[repositories] def select: Fragment

  // Required to convert queries into actual objects
  private[repositories] implicit def composite: Composite[A]

  // Simple alaias for Fragment.const
  private[repositories] def const(raw: String): Fragment = Fragment.const(raw)

  private[repositories] def getAllQuery: Query0[A] =
    select.query[A]

  def getAll: ConnectionIO[List[A]] = getAllQuery.list
}

trait CanQueryByIds[A <: BaseNode] { self: Repository[A] ⇒
  private[repositories] implicit val logHandler: LogHandler

  // Column to use as the 'id' column
  private[repositories] val idColumn: String = "uuid"

  private[repositories] val idColumnFragment: Fragment = self.const(s"$idColumn ")

  // idColumn = 'id'::uuid
  private[repositories] val idFilterFragment: UUID ⇒ Fragment =
    uuid ⇒ idColumnFragment ++ self.const(s"= ") ++ fr"$uuid::uuid".asInstanceOf[Fragment]

  private[repositories] def getByIdQuery(id: UUID): Query0[A] =
    (select ++ Fragments.whereAnd(idFilterFragment(id))).query[A]

  private[repositories] def getByIdsQuery(ids: NonEmptyList[UUID]): Query0[A] =
    (select ++ Fragments.whereAnd(Fragments.in(idColumnFragment, ids))).query[A]

  def getById(id: UUID): ConnectionIO[Option[A]] =
    getByIdQuery(id).option

  def getByIds(ids: Seq[UUID]): ConnectionIO[List[A]] =
    ids match {
      case a +: as ⇒ getByIdsQuery(NonEmptyList(a, as: _*)).list
      case _       ⇒ List.empty[A].point[ConnectionIO]
    }
}

object CanQueryRelations {
  type RelationshipQuery[Row, RelId, Cursor]  = (RelId, Option[Cursor], Long) ⇒ Query0[Row]
  type RelationshipLookup[Row, RelId, Cursor] = (RelId, Option[Cursor], Long) ⇒ ConnectionIO[List[Row]]
}

trait CanQueryRelations[A] { self: Repository[A] ⇒
  type QueryA[RelId, Cursor] = RelationshipQuery[A, RelId, Cursor]
  type LookupA[RelId, Cursor] = RelationshipLookup[A, RelId, Cursor]

  private[repositories] implicit val logHandler: LogHandler

  private[repositories] implicit def queryToLookup[RelId, Cursor](
      query: QueryA[RelId, Cursor]
    ): LookupA[RelId, Cursor] =
    (relId: RelId, cursor: Option[Cursor], limit: Long) ⇒ query(relId, cursor, limit).list

  private[repositories] def connectionQuery[RelId, Cursor](
      relColumn: String,
      cursorColumn: String,
      cursorDirection: SortDirection = ASC
    )(implicit relParam: Param[RelId],
      cursorParam: Param[Cursor]
    ): QueryA[RelId, Cursor] = {

    // Filters the result set to only things matching the relationship key
    def relColumnFilter(relId: RelId): Fragment =
      const(s"$relColumn = ") ++ fr"$relId".asInstanceOf[Fragment]

    // Adds a filter to only show items after the supplied cursor value
    def sortColumnFilter(cursor: Cursor): Fragment =
      const(s"$cursorColumn > ") ++ fr"$cursor".asInstanceOf[Fragment]

    // Adds a limit to the size specified
    def limitFragment(limit: Long): Fragment =
      fr"LIMIT $limit".asInstanceOf[Fragment]

    // Adds an order by for the given cursor column + direction
    val orderBy = const(s"ORDER BY $cursorColumn ${cursorDirection.sql} ")

    def createQuery(relId: RelId, cursor: Option[Cursor], limit: Long): Query0[A] = {
      (
        select ++
          Fragments.whereAndOpt(
            relColumnFilter(relId).some, // always filter by the rel column
            cursor.map(sortColumnFilter) // optionally add the filter for the 'after' arg
          ) ++
          // Add an order by to make after cursors work + always add a limit
          orderBy ++ limitFragment(limit)
      ).query[A]
    }

    createQuery
  }
}
