package routes

import java.net.URLEncoder
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.SessionManager
import reddit.{RedditAuthenticationApiConsumer, RedditConfig, RedditSecuredApiConsumer}
import security.Sessions
import security.Sessions.{PostAuthRegistrationSession, PreAuthRegistrationSession, RegistrationSession}
import services.{DatabaseService, UserService}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import validation.Emails

import scala.concurrent.ExecutionContext
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

class RegisterEndpoints(
    val redditConfig: RedditConfig,
    val databaseService: DatabaseService,
    val userService: UserService
  )(implicit executionContext: ExecutionContext,
    actorSystem: ActorSystem)
    extends HasRoutes
    with TwirlSupport
    with SprayJsonSupport {
  import RegistrationProtocol._
  import akka.http.scaladsl.server.Directives._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  implicit val _: SessionManager[RegistrationSession] = Sessions.registrationSessionManager

  val redditAuthenticationApi = new RedditAuthenticationApiConsumer(redditConfig)
  val redditSecuredApi        = new RedditSecuredApiConsumer(redditConfig)

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

  val redirectToFrontend: Route = redirect("/register", StatusCodes.TemporaryRedirect)

  def redirectToFrontend(error: String): Route = pass {
    val message = s"Unable to authenticate via Reddit: $error"
    redirect(s"/register/error#${URLEncoder.encode(message, "utf-8")}", StatusCodes.TemporaryRedirect)
  }

  def callback(session: PreAuthRegistrationSession): Route = parameters('code, 'state) {
    case (_, state) if state != session.state ⇒
      redirectToFrontend(error = "Mismatched state")
    case (code, _) ⇒
      // Look up username
      val usernameFuture = for {
        accessToken ← redditAuthenticationApi.getAccessToken(authCode = code)
        username    ← redditSecuredApi.getUsername(accessToken)
        inUse       ← databaseService.run(userService.isUsernameInUse(username))
      } yield (username, inUse)

      onComplete(usernameFuture) {
        case Success((_, inUse)) if inUse ⇒
          redirectToFrontend(error = "Username is already in use")
        case Success((username, _)) ⇒
          // Set username in session and redirect to the frontend for finalisation
          setSession(oneOff, usingCookies, PostAuthRegistrationSession(username)) {
            redirectToFrontend
          }
        case Failure(_) ⇒
          redirectToFrontend(error = "Failed to lookup username")
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
          parameter('error)(error ⇒ redirectToFrontend(error)) ~
            // actual callback
            callback(session) ~
            // fallback when code/state/error are not provided
            redirectToFrontend("No data provided")
        case _ ⇒
          // Either no session or invalid type
          redirectToFrontend("No data provided")
      }
    }
  }

  val registerFormSubmit: Route = (post & pathEndOrSingleSlash & entity(as[RegisterRequest])) { request ⇒
    optionalSession(oneOff, usingCookies) {
      case Some(PostAuthRegistrationSession(username)) ⇒
        onComplete(databaseService.run(userService.createUser(username, request.email, request.password))) {
          case Success(_) ⇒
            complete(StatusCodes.Created)
          case Failure(_) ⇒
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
