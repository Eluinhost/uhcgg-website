package gg.uhc.website.configuration

sealed trait ConfigurationOption
trait ServerHostConfig extends ConfigurationOption
trait ServerPortConfig extends ConfigurationOption

trait DatabaseConnectionStringConfig extends ConfigurationOption
trait DatabaseUsernameConfig         extends ConfigurationOption
trait DatabasePasswordConfig         extends ConfigurationOption

trait RedditClientIdConfig    extends ConfigurationOption
trait RedditSecretConfig      extends ConfigurationOption
trait RedditRedirectUriConfig extends ConfigurationOption
trait RedditApiQueueConfig    extends ConfigurationOption

trait RegistrationJwtDuration extends ConfigurationOption
trait ApiJwtDuration          extends ConfigurationOption

trait MaxGraphQlComplexity extends ConfigurationOption
trait MaxGraphQlDepth      extends ConfigurationOption

trait JwtSecret extends ConfigurationOption
