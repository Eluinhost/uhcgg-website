package routes

import java.net.URLEncoder
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.SessionManager
import configuration.Config
import reddit.{RedditAuthenticationApiConsumer, RedditConfig, RedditSecuredApiConsumer}
import security.Sessions
import security.Sessions.{PostAuthRegistrationSession, PreAuthRegistrationSession, RegistrationSession}
import services.{DatabaseSupport, UserService}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import validation.Emails

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object RegistrationProtocol extends DefaultJsonProtocol {
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

  implicit val registerRequestParser: RootJsonFormat[RegisterRequest] = jsonFormat2(RegisterRequest)
}

case class ParameterException(message: String) extends Exception(message)

class RegisterEndpoints(userService: UserService)(implicit actorSystem: ActorSystem)
    extends HasRoutes
    with TwirlSupport
    with SprayJsonSupport
    with DatabaseSupport {
  import RegistrationProtocol._
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val _: SessionManager[RegistrationSession] = Sessions.registrationSessionManager

  val redditConfig: RedditConfig = Config.redditConfig

  val redditAuthenticationApi = new RedditAuthenticationApiConsumer(redditConfig)(actorSystem)
  val redditSecuredApi        = new RedditSecuredApiConsumer(redditConfig)(actorSystem)

  /**
    * Sets the session to be a random state and then redirects off to reddit for authorization
    */
  val startRedditOauthFlow: Route = (get & pathEndOrSingleSlash) {
    val state = UUID.randomUUID().toString

    setSession(oneOff, usingCookies, PreAuthRegistrationSession(state)) {
      redirect(
        s"https://www.reddit.com/api/v1/authorize?client_id=${redditConfig.clientId}&response_type=code&state=$state&redirect_uri=${redditConfig.redirectUri}&duration=temporary&scope=identity",
        StatusCodes.TemporaryRedirect
      )
    }
  }

  def redirectToFrontend(username: String): Route =
    redirect(s"/register#${URLEncoder.encode(username, "utf-8")}", StatusCodes.TemporaryRedirect)

  def redirectToFrontendWithError(error: String): Route = pass {
    val message = s"Unable to authenticate via Reddit: $error"
    redirect(s"/register/error#${URLEncoder.encode(message, "utf-8")}", StatusCodes.TemporaryRedirect)
  }

  def lookupUsername(code: String)(implicit ec: ExecutionContext = Implicits.global): Future[(String, Boolean)] = for {
    accessToken ← redditAuthenticationApi.getAccessToken(authCode = code)
    username    ← redditSecuredApi.getUsername(accessToken)
    inUse       ← runQuery(userService.isUsernameInUse(username))
  } yield (username, inUse)

  def callback(session: PreAuthRegistrationSession): Route = parameters('code, 'state) {
    case (_, state) if state != session.state ⇒
      redirectToFrontendWithError("Mismatched state")
    case (code, _) ⇒
      onComplete(lookupUsername(code)) {
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
        onComplete(runQuery(userService.createUser(username, request.email, request.password))) {
          case Success(_) ⇒
            complete(StatusCodes.Created)
          case Failure(ex) ⇒
            actorSystem.log.error(ex, "Failed to add account")
            complete(StatusCodes.InternalServerError)
        }
      case _ ⇒
        // Either no session or invalid type
        complete(StatusCodes.Unauthorized)
    }
  }

  val routes: Route = pathPrefix("register") {
    startRedditOauthFlow ~ handleRedditCallback ~ registerFormSubmit
  }
}
