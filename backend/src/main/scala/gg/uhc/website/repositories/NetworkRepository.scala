package gg.uhc.website.repositories

import gg.uhc.website.model.Network

class NetworkRepository extends Repository[Network] with CanQueryByIds[Network] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Network] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, tag, description, created, modified, deleted, ownerUserId FROM networks"
      .asInstanceOf[Fragment]

  private[repositories] val getByOwnerUserIdQuery = generateConnectionQuery(relColumn = "ownerUserId")

  val getByOwnerUserId: ListConnection = genericConnectionList(getByOwnerUserIdQuery)
}
