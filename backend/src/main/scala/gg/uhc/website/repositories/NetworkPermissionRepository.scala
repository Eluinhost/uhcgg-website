package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.NetworkPermission

class NetworkPermissionRepository extends Repository[NetworkPermission] with HasRelationColumns[NetworkPermission] {
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[NetworkPermission] = implicitly[Composite[NetworkPermission]]

  override private[repositories] val baseSelect =
    select(
      p"network_id",
      p"user_id",
      p"is_admin"
    ) from p"network_permissions"

  private[repositories] val getByNetworkIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"network_id",
    sort = p"user_id".asc
  )(_)

  private[repositories] val getByUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = p"user_id",
    sort = p"network_id".asc
  )(_)

  def getByNetworkId(params: RelationshipListingParameters[UUID, UUID]): ConnectionIO[List[NetworkPermission]] =
    getByNetworkIdQuery(params).list

  def getByUserId(params: RelationshipListingParameters[UUID, UUID]): ConnectionIO[List[NetworkPermission]] =
    getByUserIdQuery(params).list
}
