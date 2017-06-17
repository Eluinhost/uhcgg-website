package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.model.BaseNode
import gg.uhc.website.schema.ForwardOnlyConnection

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

  type ConnectionQuery = (UUID, ForwardOnlyConnection) ⇒ Query0[A]
  type ListConnection = (UUID, ForwardOnlyConnection) ⇒ ConnectionIO[List[A]]

  private[repositories] implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  // Used to query for the data
  private[repositories] def select: Fragment

  // Required to convert queries into actual objects
  private[repositories] implicit def composite: Composite[A]

  // Simple alaias for Fragment.const
  private[repositories] def const(raw: String): Fragment = Fragment.const(raw)

  /**
    * Generates a query for querying relations.
    *
    * ALL PARAMETERS TO THIS *MUST* BE SAFE TO USE IN A QUERY, NO ESCAPING OCCURS.
    *
    * @return a query that takes a rel id and some connection args, these can be unsafe paramters
    */
  private[repositories] def generateConnectionQuery(
      relColumn: String,
      sortColumn: String = "uuid",
      sortColumnType: String = "uuid",
      sortDirection: SortDirection = ASC
    ): ConnectionQuery = {

    // relColumn = uuid
    val relColumnFilter: UUID ⇒ Fragment =
      id ⇒ const(s"$relColumn = ") ++ fr"$id".asInstanceOf[Fragment]

    // sortColumn > 'after'::sortColumnType
    val sortColumnFilter: String ⇒ Fragment =
      after ⇒
        const(s"$sortColumn > ") ++
          fr0"$after::".asInstanceOf[Fragment] ++
          const(s"$sortColumnType ")

    // LIMIT args.first
    val limit: ForwardOnlyConnection ⇒ Fragment =
      args ⇒ fr"LIMIT ${args.first}".asInstanceOf[Fragment]

    // ORDER BY sortColumn ASC
    val orderBy = const(s"ORDER BY $sortColumn ${sortDirection.sql} ")

    (relId: UUID, args: ForwardOnlyConnection) ⇒
      (
        select ++
          Fragments.whereAndOpt(
            relColumnFilter(relId).some, // always filter by the rel column
            args.after.map(sortColumnFilter) // optionally add the filter for the 'after' arg
          ) ++
          // Add an order by to make after cursors work + always add a limit
          orderBy ++ limit(args)
      ).query[A]
  }

  private[repositories] val genericConnectionList: (ConnectionQuery ⇒ ListConnection) =
    (query: ConnectionQuery) ⇒
      (uuid: UUID, args: ForwardOnlyConnection) ⇒
        query(uuid, args).list

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