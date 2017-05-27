package gg.uhc.website.repositories

import org.flywaydb.core.Flyway
import org.scalatest.{BeforeAndAfterAll, Suites}

class RepositoriesSpec
    extends Suites(
      new BanRepositoryTest,
      new MatchRepositoryTest,
      new MatchScenariosRepositoryTest,
      new NetworkPermissionRepositoryTest,
      new NetworkRepositoryTest,
      new RegionRepositoryTest,
      new RoleRepositoryTest,
      new ScenarioRepositoryTest,
      new ServerRepositoryTest,
      new StyleRepositoryTest,
      new UserRolesRepositoryTest,
      new UserRepositoryTest,
      new VersionRepositoryTest
    )
    with BeforeAndAfterAll
    with BaseRepositoryTest {
  override def beforeAll(): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(dataSource)
    flyway.migrate()
  }
}
