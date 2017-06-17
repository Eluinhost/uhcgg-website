package gg.uhc.website.repositories

import gg.uhc.website.model.Role

class RoleRepository extends Repository[Role] with CanQueryByIds[Role] {
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Role] = implicitly

  override private[repositories] val select: Fragment =
    fr"SELECT uuid, name, permissions FROM roles".asInstanceOf[Fragment]
}
