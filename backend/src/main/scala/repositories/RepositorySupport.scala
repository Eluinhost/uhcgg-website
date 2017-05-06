package repositories

import doobie.util.log.LogHandler

trait RepositorySupport {
  implicit val logHandler: LogHandler = LogHandler.jdkLogHandler
}
