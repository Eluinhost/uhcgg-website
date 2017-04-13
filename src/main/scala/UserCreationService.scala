import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpChallenges
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import com.softwaremill.session.{SessionConfig, SessionManager}

import scala.concurrent.ExecutionContext

class UserCreationService(private val redditConfig: RedditConfig)(
    implicit executionContext: ExecutionContext
) {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val sessionManager =
    new SessionManager[String](SessionConfig.fromConfig())

  private val redditAuthenticationError = new AuthenticationFailedRejection(
    CredentialsRejected,
    HttpChallenges.oAuth2("reddit")
  )

  val redditOauth: Route = pathPrefix("reddit") {
    (pathEndOrSingleSlash & get) { // Root should redirect to reddit and start authentication flow
      val session = UUID.randomUUID().toString

      setSession(oneOff, usingCookies, session) {
        redirect(
          s"https://www.reddit.com/api/v1/authorize?client_id=${redditConfig.clientId}&response_type=code&state=$session&redirect_uri=${redditConfig.redirectUri}&duration=temporary&scope=identity",
          StatusCodes.TemporaryRedirect
        )
      }
    } ~ (pathPrefix("redirect") & pathEndOrSingleSlash & get) {
      // Check if there was an error provided first
      parameter('error) { _ ⇒
        // Invalidate the session as it was failed
        invalidateSession(oneOff, usingCookies) {
          reject(redditAuthenticationError)
        }
      } ~ (parameters('code, 'state) & requiredSession(oneOff, usingCookies)) { // Passed a valid code + state parameter + session is valid
        (code: String, state: String, session: String) ⇒
          // Invalidate the session now it is no longer required
          invalidateSession(oneOff, usingCookies) {
            if (state != session) { // Check the state sent to reddit matches what was in the session
              reject(redditAuthenticationError)
            } else {
              complete {
                code + " / " + state + " / " + session.toString
              }

              // TODO request access code from external service using code
              // TODO verify scope
              // TODO lookup user name from external service using access code
              // TODO store user name in session
              // TODO show page with password creation form
              // TODO password creation form should then create user entry + log user in + clear session
            }
          }
      } ~ invalidateSession(oneOff, usingCookies) { // Invalidate the session as there was a failure
        reject(redditAuthenticationError)
      }
    }
  }

  val internalOauth: Route = pathPrefix("internal") {
    complete {
      "todo" // TODO entire route
    }
  }

  val routes: Route = pathPrefix("oauth") {
    redditOauth ~ internalOauth
  }
}
