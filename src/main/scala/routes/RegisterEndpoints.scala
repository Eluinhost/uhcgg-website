package routes

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.{SessionConfig, SessionManager}
import reddit.{RedditAuthenticationApiConsumer, RedditAuthenticationException, RedditConfig, RedditSecuredApiConsumer}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContext

case class RegisterRequest(email: String, password: String, confirm: String) {
  require(password == confirm, "Passwords do not match")
  require(
    "[a-z]+".r.findFirstIn(password).isDefined,
    "Password does not contain at least 1 lower case character"
  )
  require(
    "[A-Z]+".r.findFirstIn(password).isDefined,
    "Password does not contain at least 1 upper case character"
  )
  require(
    "[0-9]+".r.findFirstIn(password).isDefined,
    "Password does not contain at least 1 digit character"
  )
  require(
    "[^a-zA-Z0-9]+".r.findFirstIn(password).isDefined,
    "Password does not contain at least 1 special character"
  )
  require(
    email.matches("HAH"),
    "Invalid email provided"
  )
}

case class ParameterException(message: String) extends Exception(message)

class RegisterEndpoints(
    val redditConfig: RedditConfig
  )(implicit executionContext: ExecutionContext,
    actorSystem: ActorSystem)
    extends HasRoutes
    with TwirlSupport
    with SprayJsonSupport
    with DefaultJsonProtocol {
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val registerRequestParser: RootJsonFormat[RegisterRequest] = jsonFormat3(RegisterRequest)

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
                      redirect("/register/finalise", StatusCodes.TemporaryRedirect)
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

  val finaliseRoutes: Route =
    requiredSession(oneOff, usingCookies) { session: Map[String, String] ⇒
      session.get("username") match {
        case Some(username) ⇒
          // TODO check if username is already registered (both get & post)
          get {
            complete(html.react("register"))
          } ~ (post & entity(as[RegisterRequest])) { request ⇒
            complete(s"$request")
          }
        case None ⇒
          redirect("/register", StatusCodes.TemporaryRedirect) // redirect to start of the flow, we have no username in session
      }
    }

  val routes: Route =
    (get & pathEndOrSingleSlash) {
      startRedditOauthFlow
    } ~ (get & pathPrefix("callback") & pathEndOrSingleSlash) { // Handles redirects from Reddit after authorization
      handleRedditCallback
    } ~ ((get | post) & pathPrefix("finalise") & pathEndOrSingleSlash) {
      finaliseRoutes
    }
}
