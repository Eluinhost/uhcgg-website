package routes

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.{SessionConfig, SessionManager}
import reddit.{RedditAuthenticationApiConsumer, RedditAuthenticationException, RedditConfig, RedditSecuredApiConsumer}

import scala.concurrent.ExecutionContext

class RegisterEndpoints(val redditConfig: RedditConfig)(
    implicit executionContext: ExecutionContext,
    actorSystem: ActorSystem
) extends HasRoutes
    with TwirlSupport {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val sessionManager = new SessionManager[Map[String, String]](SessionConfig.fromConfig())

  val redditAuthenticationApi = new RedditAuthenticationApiConsumer(redditConfig)
  val redditSecuredApi        = new RedditSecuredApiConsumer(redditConfig)

  /**
    * Handles RedditAuthenticationExceptions and sends an UNAUTHORIZED message in place
    * TODO page styles
    */
  val redditExceptionHandler = ExceptionHandler {
    case t: RedditAuthenticationException ⇒
      actorSystem.log.error(t, "Unable to authenticate via reddit")
      complete(StatusCodes.Unauthorized, html.registerError.render(s"Unable to authenticate via Reddit: ${t.message}"))
  }

  /**
    * Sets the session to be a random state and then redirects off to reddit for authorization
    */
  def startRedditOauthFlow: Route = {
    val state = UUID.randomUUID().toString

    setSession(oneOff, usingCookies, Map("state" → state)) {
      redirect(
        s"https://www.reddit.com/api/v1/authorize?client_id=${redditConfig.clientId}&response_type=code&state=$state&redirect_uri=${redditConfig.redirectUri}&duration=temporary&scope=identity",
        StatusCodes.TemporaryRedirect
      )
    }
  }

  /**
    * Route that Reddit redirects users to after authorization
    */
  def handleRedditCallback: Route =
    // Get the data from the session and immediately invalidate it
    requiredSession(oneOff, usingCookies) { session: Map[String, String] ⇒
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

            // Check state matches what is in the session
            session.get("state") match {
              case Some(stored) if state == stored ⇒
                // Look up username
                val usernameFuture = for {
                  accessToken ← redditAuthenticationApi.getAccessToken(authCode = code)
                  username    ← redditSecuredApi.getUsername(accessToken)
                } yield username

                onComplete(usernameFuture) {
                  _.map { username ⇒
                    setSession(oneOff, usingCookies, Map("username" → username)) {
                      redirect("register/finalise", StatusCodes.TemporaryRedirect)
                    }
                  }.getOrElse {
                    failWith(RedditAuthenticationException("Failed to lookup username"))
                  }
                }
              case _ ⇒ failWith(RedditAuthenticationException("Invalid state"))
            }
          }

          // If neither match show error for no data in request
          val fallback = failWith(RedditAuthenticationException("No provided data"))

          errors ~ process ~ fallback
        }
      }
    }

  val routes: Route =
    (get & pathEndOrSingleSlash) {
      startRedditOauthFlow
    } ~ (get & pathPrefix("callback") & pathEndOrSingleSlash) { // Handles redirects from Reddit after authorization
      handleRedditCallback
    } ~ (pathPrefix("finalise") & pathEndOrSingleSlash) { // TODO: this is the 'register' page with form fields

      // TODO must have a valid session with user name stored in it
      // TODO session should probably be encrypted for this to be secure...
      // TODO password creation form should then create user entry + log user in + clear session
      get {
        complete(html.register.render())
      } ~ post {
        // TODO process data and only rerender if there was an error
        complete(html.register.render())
      }
    }
}
