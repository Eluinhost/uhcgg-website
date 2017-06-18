package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.UserRole

class UserRolesRepository extends Repository[UserRole] with CanQueryRelations[UserRole] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[UserRole] = implicitly

  private[repositories] val select: Fragment =
    fr"SELECT userId, roleId FROM user_roles".asInstanceOf[Fragment]

  private[repositories] val getByUserIdQuery = connectionQuery[UUID, UUID](
    relColumn = "userId",
    cursorColumn = "roleId"
  )
  private[repositories] val getByRoleIdQuery = connectionQuery[UUID, UUID](
    relColumn = "roleId",
    cursorColumn = "userId"
  )

  private[repositories] val getAllByUserIdQuery = (userId: UUID) ⇒
    (select ++ Fragments.whereAnd(fr"userId = $userId".asInstanceOf[Fragment])).query[UserRole]

  val getByUserId: LookupA[UUID, UUID] = getByUserIdQuery
  val getByRoleId: LookupA[UUID, UUID] = getByRoleIdQuery

  val getAllByUserId: (UUID) ⇒ ConnectionIO[List[UserRole]] =
    (userId: UUID) ⇒ getAllByUserIdQuery(userId).list
}
