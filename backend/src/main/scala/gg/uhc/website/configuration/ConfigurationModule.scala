package gg.uhc.website.configuration

import java.time.Duration

import com.typesafe.config.ConfigFactory
import com.softwaremill.tagging._
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm

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

  lazy val redditApiQueueConfig: Int @@ RedditApiQueueConfig = {
    config.getInt("reddit.queueSize").taggedWith[RedditApiQueueConfig]
  }

  lazy val jwtSecret: String @@ JwtSecret = {
    config.getString("jwt.secret").taggedWith[JwtSecret]
  }

  lazy val registrationJwtDuration: Duration @@ RegistrationJwtDuration = {
    config.getDuration("jwt.registration-timeout").taggedWith[RegistrationJwtDuration]
  }

  lazy val apiJwtDuration: Duration @@ ApiJwtDuration = {
    config.getDuration("jwt.api-timeout").taggedWith[ApiJwtDuration]
  }

  lazy val jwtAlgo: JwtHmacAlgorithm = {
    JwtAlgorithm.fromString(config.getString("jwt.algorithm")) match {
      case e: JwtHmacAlgorithm ⇒ e
      case _ ⇒ throw new IllegalArgumentException("Expected a HMAC algorithm")
    }
  }
}
