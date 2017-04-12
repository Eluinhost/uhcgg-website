import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

class DatabaseService(jdbcUrl: String, dbUser: String, dbPassword: String) {
  private[this] val config = new HikariConfig()

  config.setJdbcUrl(jdbcUrl)
  config.setUsername(dbUser)
  config.setPassword(dbPassword)

  val dataSource = new HikariDataSource(config)
}
