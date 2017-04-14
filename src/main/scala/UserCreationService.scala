import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.softwaremill.session.{SessionConfig, SessionManager}
import reddit.{RedditApi, RedditAuthenticationException, RedditConfig}

import scala.concurrent.ExecutionContext

class UserCreationService(
    val redditConfig: RedditConfig
  )(implicit executionContext: ExecutionContext,
    actorSystem: ActorSystem) {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val sessionManager = new SessionManager[String](SessionConfig.fromConfig())

  val redditApi = new RedditApi(redditConfig)

  /**
    * Sets the session to be a random state and then redirects off to reddit for authorization
    */
  def startRedditOauthFlow: Route = {
    val session = UUID.randomUUID().toString

    setSession(oneOff, usingCookies, session) {
      redirect(
        s"https://www.reddit.com/api/v1/authorize?client_id=${redditConfig.clientId}&response_type=code&state=$session&redirect_uri=${redditConfig.redirectUri}&duration=temporary&scope=identity",
        StatusCodes.TemporaryRedirect
      )
    }
  }

  /**
    * Handles RedditAuthenticationExceptions and sends an UNAUTHORIZED message in place
    * TODO page styles
    */
  val redditExceptionHandler = ExceptionHandler {
    case t: RedditAuthenticationException ⇒
      actorSystem.log.error(t, "Unable to authenticate via reddit")
      complete(HttpResponse(StatusCodes.Unauthorized, entity = s"Unable to authenticate via Reddit: ${t.message}"))
  }

  /**
    * Route that Reddit redirects users to after authorization
    */
  def handleRedditLogin: Route =
    // Get the data from the session and immediately invalidate it
    requiredSession(oneOff, usingCookies) { session: String ⇒
      invalidateSession(oneOff, usingCookies) {

        // Handle RedditAuthenticationExceptions that happen under this route
        handleExceptions(redditExceptionHandler) {

          // If an error sent back, reject
          val errors = parameter('error) { error ⇒
            failWith(RedditAuthenticationException(message = error))
          }

          // Valid is code/state combo sent back
          val process = parameters('code, 'state) { (code: String, state: String) ⇒
            actorSystem.log.debug(s"Handling redirect from reddit. code:$code state:$state session:$session")

            // Validate the state sent to reddit matches what was in the session
            if (state != session) {
              return failWith(RedditAuthenticationException("State Mismatch"))
            }

            // Look up username
            val usernameFuture = for {
              accessToken ← redditApi.getAccessToken(authCode = code)
              username    ← redditApi.queryUsername(accessToken)
            } yield username

            onComplete(usernameFuture) {
              _.map { username ⇒
                complete(s"$username / $code / $state / $session")
              // TODO store user name in session
              // TODO show page with password creation form
              // TODO password creation form should then create user entry + log user in + clear session
              }.getOrElse {
                failWith(RedditAuthenticationException("Failed to lookup username"))
              }
            }

          }

          // If neither match show error for no data in request
          val fallback = failWith(RedditAuthenticationException("No provided data"))

          errors ~ process ~ fallback
        }
      }
    }

  val redditOauth: Route =
    (pathEndOrSingleSlash & get) { // Root should redirect to reddit and start authentication flow
      startRedditOauthFlow
    } ~ (pathPrefix("redirect") & pathEndOrSingleSlash & get) { // Redirect handles redirects after authorization
      handleRedditLogin
    }

  val internalOauth: Route = pathPrefix("internal") {
    complete {
      "todo" // TODO entire route
    }
  }

  val routes: Route = pathPrefix("oauth") {
    pathPrefix("reddit") {
      redditOauth
    } ~ pathPrefix("internal") {
      internalOauth
    }
  }
}
