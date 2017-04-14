import org.flywaydb.core.Flyway

class MigrationsService(jdbcUrl: String, dbUser: String, dbPassword: String) {
  private[this] val flyway = new Flyway()
  flyway.setDataSource(jdbcUrl, dbUser, dbPassword)

  def migrate(): Unit = flyway.migrate()

  def drop(): Unit = flyway.clean()
}
