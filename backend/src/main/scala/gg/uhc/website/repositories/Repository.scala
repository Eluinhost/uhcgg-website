package gg.uhc.website.repositories

import java.util.UUID

import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.model.BaseNode
import sangria.execution.deferred.{Relation, RelationIds}

import scalaz.Scalaz._
import scalaz._

trait Repository[A] {
  implicit val logHandler: LogHandler = LogHandler.jdkLogHandler

  private[repositories] def const(raw: String): Fragment = Fragment.const(raw)

  private[repositories] def columnIn[T : Param](column: String, columnType: String, values: NonEmptyList[T]) =
    values
      .map(v ⇒ fr0"$v".asInstanceOf[Fragment] ++ const(s"::$columnType"))
      .foldSmash1(
        const(s"$column IN ("),
        const(","),
        const(")")
      )
}

trait CanQuery[A] { self: Repository[A] ⇒
  private[repositories] def baseSelectQuery: Fragment

  implicit def composite: Composite[A]
}

trait CanQueryByIds[A <: BaseNode] { self: CanQuery[A] with Repository[A] ⇒
  implicit val logHandler: LogHandler

  private[repositories] def getByIdQuery(id: UUID): Query0[A] =
    (baseSelectQuery ++ Fragments.whereAnd(fr0"uuid = $id".asInstanceOf[Fragment])).query[A]

  private[repositories] def getByIdsQuery(ids: NonEmptyList[UUID]): Query0[A] =
    (baseSelectQuery ++ Fragments.whereAnd(Fragments.in(fr"uuid".asInstanceOf[Fragment], ids))).query[A]

  def getById(id: UUID): ConnectionIO[Option[A]] =
    getByIdQuery(id).option

  def getByIds(ids: Seq[UUID]): ConnectionIO[List[A]] =
    ids match {
      case a +: as ⇒ getByIdsQuery(NonEmptyList(a, as: _*)).list
      case _       ⇒ List.empty[A].point[ConnectionIO]
    }
}

trait CanQueryAll[A] { self: CanQuery[A] ⇒
  private[repositories] def getAllQuery: Query0[A] = baseSelectQuery.query[A]

  implicit val logHandler: LogHandler

  def getAll: ConnectionIO[List[A]] = getAllQuery.list
}

trait CanQueryByRelations[A] { self: CanQuery[A] with Repository[A] ⇒
  implicit val logHandler: LogHandler

  def relationsFragment(relationIds: RelationIds[A]): Fragment

  protected def buildRelationIds(map: Map[Relation[A, _, _], Option[Seq[_]]]): RelationIds[A] =
    RelationIds(
      map
        .filter(_._2.isDefined)
        .mapValues(_.get)
    )

  protected def simpleRelationFragment[RelId: Param](
      relIds: RelationIds[A],
      rel: Relation[A, _, RelId],
      column: String,
      columnType: String
    ): Option[Fragment] =
    for {
      ids ← relIds.get(rel)
      nel ← ids.toList.toNel
    } yield columnIn(column, columnType, nel)

  private[repositories] def relationsQuery(relationIds: RelationIds[A]): Query0[A] =
    (baseSelectQuery ++ relationsFragment(relationIds)).query[A]

  def getByRelations(rel: RelationIds[A]): ConnectionIO[List[A]] =
    relationsQuery(rel).list
}
