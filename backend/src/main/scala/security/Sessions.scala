package security

import com.softwaremill.session._
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

object Sessions {
  sealed trait RegistrationSession
  case class PreAuthRegistrationSession(state: String)     extends RegistrationSession
  case class PostAuthRegistrationSession(username: String) extends RegistrationSession

  private val config = ConfigFactory.load()

  private implicit val registrationSessionSerializer = new MultiValueSessionSerializer[RegistrationSession](
    toMap = {
      case PreAuthRegistrationSession(state)     ⇒ Map("type" → "pre", "value"  → state)
      case PostAuthRegistrationSession(username) ⇒ Map("type" → "post", "value" → username)
    },
    fromMap = (m: Map[String, String]) ⇒ {
      val stored = for {
        t ← m.get("type")
        v ← m.get("value")
      } yield t → v

      stored
        .map {
          case (t, v) if t == "pre"  ⇒ PreAuthRegistrationSession(v)
          case (t, v) if t == "post" ⇒ PostAuthRegistrationSession(v)
        }
        .map(Success(_))
        .getOrElse(Failure(new IllegalArgumentException(s"Invalid data $m")))
    }
  )
  private implicit val registrationSessionEncoder = new BasicSessionEncoder[RegistrationSession]()

  val registrationSessionManager =
    new SessionManager[RegistrationSession](SessionConfig.fromConfig(config.getConfig("registration")))

}
