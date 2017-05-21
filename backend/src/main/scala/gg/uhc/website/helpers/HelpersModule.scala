package services

import com.softwaremill.macwire.wire
import gg.uhc.website.configuration.ConfigurationModule
import gg.uhc.website.helpers.reddit._

trait HelpersModule extends ConfigurationModule {
  lazy val authenticationApi: RedditAuthenticationApi = wire[RedditAuthenticationApi]
  lazy val securedApi: RedditSecuredApi               = wire[RedditSecuredApi]
}
