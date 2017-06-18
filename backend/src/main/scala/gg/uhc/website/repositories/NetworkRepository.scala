package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.model.Network

class NetworkRepository extends Repository[Network] with HasUuidIdColumn[Network] with HasRelationColumns[Network] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Network] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, tag, description, created, modified, deleted, ownerUserId FROM networks"
      .asInstanceOf[Fragment]

  private[repositories] val getByOwnerUserIdQuery = relationListingQuery[UUID, UUID](
    relColumn = "ownerUserId",
    cursorColumn = "uuid"
  )

  val getByOwnerUserId: LookupA[UUID, UUID] = getByOwnerUserIdQuery
}
