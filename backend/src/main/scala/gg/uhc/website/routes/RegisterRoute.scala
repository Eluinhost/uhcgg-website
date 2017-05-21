package gg.uhc.website.routes

import java.net.URLEncoder
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.SessionOptions.{oneOff, usingCookies}
import com.softwaremill.session.{SessionDirectives, SessionManager}
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.helpers.reddit.{RedditAuthenticationApi, RedditSecuredApi}
import gg.uhc.website.repositories.UserRepository
import gg.uhc.website.security.Sessions
import gg.uhc.website.security.Sessions.{PostAuthRegistrationSession, PreAuthRegistrationSession, RegistrationSession}
import gg.uhc.website.validation.Emails

import scala.util.{Failure, Success}

case class RegisterRequest(email: String, password: String) {
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
    password.length >= 8,
    "Password must contain at least 8 characters"
  )
  require(
    Emails.isValid(email),
    "Invalid email provided"
  )
}

case class ParameterException(message: String) extends Exception(message)

class RegisterRoute(
    userRepository: UserRepository,
    redditAuthenticationApi: RedditAuthenticationApi,
    redditSecuredApi: RedditSecuredApi)
    extends PartialRoute
    with TwirlSupport
    with CustomJsonCodec
    with SessionDirectives {

  implicit val _: SessionManager[RegistrationSession] = Sessions.registrationSessionManager

  /**
    * Sets the session to be a random state and then redirects off to helpers.reddit for authorization
    */
  val startRedditOauthFlow: Route = (get & pathEndOrSingleSlash) {
    val state = UUID.randomUUID().toString

    setSession(oneOff, usingCookies, PreAuthRegistrationSession(state)) {
      redirect(redditAuthenticationApi.startAuthFlowUrl(state), StatusCodes.TemporaryRedirect)
    }
  }

  def redirectToFrontend(username: String): Route =
    redirect(s"/register#${URLEncoder.encode(username, "utf-8")}", StatusCodes.TemporaryRedirect)

  def redirectToFrontendWithError(error: String): Route = pass {
    val message = s"Unable to authenticate via Reddit: $error"
    redirect(s"/register/error#${URLEncoder.encode(message, "utf-8")}", StatusCodes.TemporaryRedirect)
  }

  def callback(session: PreAuthRegistrationSession): Route = parameters('code → 'state) {
    case (_, state) if state != session.state ⇒
      redirectToFrontendWithError("Mismatched state")
    case (code, _) ⇒
      extractExecutionContext { implicit ec ⇒
        val task = for {
          accessToken ← redditAuthenticationApi.getAccessToken(authCode = code)
          username    ← redditSecuredApi.getUsername(accessToken)
          inUse       ← userRepository.isUsernameInUse(username)
        } yield (username, inUse)

        onComplete(task) {
          case Success((_, inUse)) if inUse ⇒
            redirectToFrontendWithError("Username is already in use")
          case Success((username, _)) ⇒
            // Set username in session and redirect to the frontend for finalisation
            setSession(oneOff, usingCookies, PostAuthRegistrationSession(username)) {
              redirectToFrontend(username)
            }
          case Failure(_) ⇒
            redirectToFrontendWithError("Failed to lookup username")
        }
      }
  }

  /**
    * Route that Reddit redirects users to after authorization
    */
  val handleRedditCallback: Route = (get & path("callback")) {
    // Always invalidate session after request
    invalidateSession(oneOff, usingCookies) {
      optionalSession(oneOff, usingCookies) {
        case Some(session: PreAuthRegistrationSession) ⇒
          // check error paramter first
          parameter('error)(error ⇒ redirectToFrontendWithError(error)) ~
            // actual callback
            callback(session) ~
            // fallback when code/state/error are not provided
            redirectToFrontendWithError("No data provided")
        case _ ⇒
          // Either no session or invalid type
          redirectToFrontendWithError("No data provided")
      }
    }
  }

  val registerFormSubmit: Route = (post & pathEndOrSingleSlash & entity(as[RegisterRequest])) { request ⇒
    optionalSession(oneOff, usingCookies) {
      case Some(PostAuthRegistrationSession(username)) ⇒
        onComplete(userRepository.createUser(username, request.email, request.password)) {
          case Success(_) ⇒
            complete(StatusCodes.Created)
          case Failure(ex) ⇒
            extractLog { logger ⇒
              logger.error(ex, "Failed to add account")
              complete(StatusCodes.InternalServerError)
            }
        }
      case _ ⇒
        // Either no session or invalid type
        complete(StatusCodes.Unauthorized)
    }
  }

  val route: Route = pathPrefix("register") {
    startRedditOauthFlow ~ handleRedditCallback ~ registerFormSubmit
  }
}
