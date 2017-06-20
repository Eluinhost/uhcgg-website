package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.UserRole

class UserRolesRepository extends Repository[UserRole] with HasRelationColumns[UserRole] {
  import doobie.imports._
  import doobie.postgres.imports._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._

  override private[repositories] val composite: Composite[UserRole] = implicitly[Composite[UserRole]]

  override private[repositories] val baseSelect =
    select(
      p"user_id",
      p"role_id"
    ) from p"user_roles"

  private[repositories] val getByUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"user_id",
    sort = p"role_id".asc
  )
  private[repositories] val getByRoleIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"role_id",
    sort = p"user_id".asc
  )

  private[repositories] def getAllByUserIdQuery(userId: UUID) =
    (baseSelect where (p"user_id" === userId)).build.query[UserRole]

  val getByUserId: LookupA[UUID, UUID] = getByUserIdQuery
  val getByRoleId: LookupA[UUID, UUID] = getByRoleIdQuery

  def getAllByUserId(userId: UUID): ConnectionIO[List[UserRole]] =
    getAllByUserIdQuery(userId).list
}
