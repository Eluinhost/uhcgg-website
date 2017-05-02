package configuration

import com.typesafe.config.ConfigFactory
import com.softwaremill.tagging._

trait ConfigurationModule {
  private[this] val config = ConfigFactory.load()

  lazy val serverHostConfig: String @@ ServerHostConfig = {
    config.getString("http.interface").taggedWith[ServerHostConfig]
  }

  lazy val serverPortConfig: Int @@ ServerPortConfig = {
    config.getInt("http.port").taggedWith[ServerPortConfig]
  }

  lazy val databaseConnectionStringConfig: String @@ DatabaseConnectionStringConfig = {
    config.getString("database.url").taggedWith[DatabaseConnectionStringConfig]
  }

  lazy val databaseUsernameConfig: String @@ DatabaseUsernameConfig = {
    config.getString("database.user").taggedWith[DatabaseUsernameConfig]
  }

  lazy val databasePasswordConfig: String @@ DatabasePasswordConfig = {
    config.getString("database.password").taggedWith[DatabasePasswordConfig]
  }

  lazy val redditClientIdConfig: String @@ RedditClientIdConfig = {
    config.getString("reddit.clientId").taggedWith[RedditClientIdConfig]
  }

  lazy val redditSecretConfig: String @@ RedditSecretConfig = {
    config.getString("reddit.clientSecret").taggedWith[RedditSecretConfig]
  }

  lazy val redditRedirectUriConfig: String @@ RedditRedirectUriConfig = {
    config.getString("reddit.redirectUri").taggedWith[RedditRedirectUriConfig]
  }
}
