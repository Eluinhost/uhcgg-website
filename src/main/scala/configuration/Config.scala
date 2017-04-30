package configuration

import com.typesafe.config.ConfigFactory
import reddit.RedditConfig

object Config {
  private val config         = ConfigFactory.load()
  private val httpConfig     = config.getConfig("http")
  private val databaseConfig = config.getConfig("database")

  val httpHost: String = httpConfig.getString("interface")
  val httpPort: Int    = httpConfig.getInt("port")

  val jdbcUrl: String    = databaseConfig.getString("url")
  val dbUser: String     = databaseConfig.getString("user")
  val dbPassword: String = databaseConfig.getString("password")

  private val redditConfigSection = config.getConfig("reddit")

  val redditConfig = RedditConfig(
    redditConfigSection.getString("clientId"),
    redditConfigSection.getString("clientSecret"),
    redditConfigSection.getString("redirectUri")
  )
}
