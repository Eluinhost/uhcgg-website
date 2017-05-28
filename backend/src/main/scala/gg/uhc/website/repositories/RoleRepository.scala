package gg.uhc.website.repositories

import gg.uhc.website.model.Role

class RoleRepository
    extends Repository[Role]
    with CanQuery[Role]
    with CanQueryByIds[Int, Role]
    with CanQueryAll[Role] {
  import doobie.imports._
  import doobie.postgres.imports._

  override val composite: Composite[Role] = implicitly
  override val idParam: Param[Int]        = implicitly

  override private[repositories] val baseSelectQuery: Fragment =
    fr"SELECT id, name, permissions FROM roles".asInstanceOf[Fragment]
}
