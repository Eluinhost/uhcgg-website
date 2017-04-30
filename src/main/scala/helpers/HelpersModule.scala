package services

import com.softwaremill.macwire.wire
import configuration.ConfigurationModule
import helpers.reddit._

trait HelpersModule extends ConfigurationModule {
  lazy val userService: UserHelper = wire[UserHelper]

  lazy val authenticationApi: RedditAuthenticationApi = wire[RedditAuthenticationApi]
  lazy val securedApi: RedditSecuredApi               = wire[RedditSecuredApi]
}
