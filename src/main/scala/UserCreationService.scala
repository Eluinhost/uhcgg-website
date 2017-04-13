import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.softwaremill.session.{SessionConfig, SessionManager}

import scala.concurrent.ExecutionContext

class UserCreationService(private val redditConfig: RedditConfig)(implicit executionContext: ExecutionContext) {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._
  import scala.concurrent.duration._

  implicit val sessionManager = new SessionManager[Map[String, String]](SessionConfig.fromConfig())

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
              s"https://www.reddit.com/api/v1/authorize?client_id=${redditConfig.clientId}&response_type=code&state=$state&redirect_uri=${redditConfig.redirectUri}&duration=temporary&scope=$scope",
              StatusCodes.TemporaryRedirect
            )
          }
        } ~
        (pathPrefix("redirect") & pathEndOrSingleSlash & get) {
          parameter('error) { error ⇒
            complete {
              s"Redirect error $error"
            }
          } ~
          (parameters('code, 'state) & requiredSession(oneOff, usingCookies)) { // TODO different display errors if session missing
            (code, state, session) ⇒
              complete {
                code + " / " + state + " / " + session.toString()
                // TODO verify state
                // TODO request access code from external service using code
                // TODO verify scope
                // TODO lookup user name from external service using access code
                // TODO store user name in session
                // TODO show page with password creation form
                // TODO password creation form should then create user entry + log user in + clear session
              }
          } ~
          complete {
            "invalid response"
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
