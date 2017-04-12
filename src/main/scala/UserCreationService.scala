import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.softwaremill.session.{SessionConfig, SessionManager}

import scala.concurrent.ExecutionContext

class UserCreationService(implicit executionContext: ExecutionContext) {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  import scala.concurrent.duration._

  private val sessionConfig = SessionConfig.fromConfig() // TODO change to pull from config
  implicit val sessionManager = new SessionManager[Map[String, String]](sessionConfig)

  val routes: Route =
    pathPrefix("oauth") {
      pathPrefix("external") {
        (pathEndOrSingleSlash & get) {
          val state = UUID.randomUUID().toString
          val scope = "identity"
          val expires = (System.currentTimeMillis() / 1000) + (10 minutes).toSeconds

          val session = Map(
            "state" → state,
            "scope" → scope,
            "expires" → expires.toString
          )

          setSession(oneOff, usingCookies, session) {
            redirect(
              s"https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&state=$state&redirect_uri=test&duration=permanent&scope=$scope",
              StatusCodes.TemporaryRedirect
            )
          }
        } ~
        (pathPrefix("redirect") & pathEndOrSingleSlash & get & requiredSession(oneOff, usingCookies)) {
          session ⇒
            complete {
              session.toString()
              // TODO parse code/state from request
              // TODO verify state
              // TODO request access code from external service using code
              // TODO verify scope
              // TODO lookup user name from external service using access code
              // TODO store user name in session
              // TODO show page with password creation form
              // TODO password creation form should then create user entry + log user in + clear session
          }
        }
      } ~
      pathPrefix("internal") {
        complete {
          "todo" // TODO entire route
        }
      }
    }
}
