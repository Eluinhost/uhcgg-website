package gg.uhc.website.security

import com.softwaremill.macwire._
import gg.uhc.website.configuration.ConfigurationModule

trait SecurityModule extends ConfigurationModule {
  lazy val apiSession: ApiSession                   = wire[ApiSession]
  lazy val registrationSession: RegistrationSession = wire[RegistrationSession]
}
