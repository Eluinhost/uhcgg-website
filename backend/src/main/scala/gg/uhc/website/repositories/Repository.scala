package gg.uhc.website.repositories

import doobie.imports._
import doobie.postgres.imports._
import gg.uhc.website.schema.model.IdentificationFields
import sangria.execution.deferred.{Relation, RelationIds}

import scalaz.Scalaz._
import scalaz._

trait Repository[A] {
  implicit val logHandler: LogHandler = LogHandler.jdkLogHandler
}

trait CanQuery[A] { self: Repository[A] ⇒
  private[repositories] def baseSelectQuery: Fragment

  implicit def composite: Composite[A]
}

trait CanQueryByIds[ID, A <: IdentificationFields[ID]] { self: CanQuery[A] ⇒
  implicit def idParam: Param[ID]

  implicit val logHandler: LogHandler

  private[repositories] def getByIdQuery(id: ID): Query0[A] =
    (baseSelectQuery ++ Fragments.whereAnd(fr"id = $id".asInstanceOf[Fragment])).query[A]

  private[repositories] def getByIdsQuery(ids: NonEmptyList[ID]): Query0[A] =
    (baseSelectQuery ++ Fragments.whereAnd(Fragments.in(fr"id".asInstanceOf[Fragment], ids))).query[A]

  def getById(id: ID): ConnectionIO[Option[A]] =
    getByIdQuery(id).option

  def getByIds(ids: Seq[ID]): ConnectionIO[List[A]] =
    ids match {
      case a +: as ⇒ getByIdsQuery(NonEmptyList(a, as: _*)).list
      case _       ⇒ List.empty[A].η[ConnectionIO]
    }
}

trait CanQueryAll[A] { self: CanQuery[A] ⇒
  private[repositories] def getAllQuery: Query0[A] = baseSelectQuery.query[A]

  implicit val logHandler: LogHandler

  def getAll: ConnectionIO[List[A]] = getAllQuery.list
}

trait CanQueryByRelations[A] { self: CanQuery[A] ⇒
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
      column: String
    ): Option[Fragment] =
    for {
      ids ← relIds.get(rel)
      nel ← ids.toList.toNel
    } yield Fragments.in(Fragment.const(column + " "), nel)

  private[repositories] def relationsQuery(relationIds: RelationIds[A]): Query0[A] =
    (baseSelectQuery ++ relationsFragment(relationIds)).query[A]

  def getByRelations(rel: RelationIds[A]): ConnectionIO[List[A]] =
    relationsQuery(rel).list
}
