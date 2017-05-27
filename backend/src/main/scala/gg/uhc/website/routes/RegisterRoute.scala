package gg.uhc.website.routes

import java.net.URLEncoder

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.database.DatabaseRunner
import gg.uhc.website.helpers.reddit.{RedditAuthenticationApi, RedditSecuredApi}
import gg.uhc.website.repositories.UserRepository
import gg.uhc.website.security.RegistrationSession
import gg.uhc.website.validation.EmailValidation._

import scala.util.{Failure, Success}

case class RegisterRequest(email: String, password: String, token: String) {
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
    email.isValidEmailFormat,
    "Invalid email provided"
  )
}

case class ParameterException(message: String) extends Exception(message)

class RegisterRoute(
    userRepository: UserRepository,
    redditAuthenticationApi: RedditAuthenticationApi,
    redditSecuredApi: RedditSecuredApi,
    registrationSession: RegistrationSession,
    databaseRunner: DatabaseRunner)
    extends PartialRoute
    with TwirlSupport
    with CustomJsonCodec {

  /**
    * Redirects off to helpers.reddit for authorization
    */
  def startRedditOauthFlow: Route = redirect(
    redditAuthenticationApi.startAuthFlowUrl(registrationSession.generateStage1Token()),
    StatusCodes.TemporaryRedirect
  )

  def redirectToFrontend(username: String): Route = redirect(
    s"/register#${registrationSession.generateStage2Token(username)}",
    StatusCodes.TemporaryRedirect
  )

  def redirectToFrontendWithError(error: String): Route = pass {
    val message = s"Unable to authenticate via Reddit: $error"
    redirect(
      s"/register/error#${URLEncoder.encode(message, "utf-8")}",
      StatusCodes.TemporaryRedirect
    )
  }

  def callback(code: String, state: String): Route =
    registrationSession.parseDataFromToken(state) match {
      case None ⇒ redirectToFrontendWithError("Invalid token data supplied")
      case Some(_) ⇒
        extractExecutionContext { implicit ec ⇒
          val task = for {
            accessToken ← redditAuthenticationApi.getAccessToken(authCode = code)
            username    ← redditSecuredApi.getUsername(accessToken)
            inUse       ← databaseRunner(userRepository.isUsernameInUse(username))
          } yield (username, inUse)

          onComplete(task) {
            case Success((_, inUse)) if inUse ⇒
              redirectToFrontendWithError("Username is already in use")
            case Success((username, _)) ⇒
              redirectToFrontend(username)
            case Failure(_) ⇒
              redirectToFrontendWithError("Failed to lookup username")
          }
        }
    }

  /**
    * Route that Reddit redirects users to after authorization
    */
  val handleRedditCallback: Route =
    // check for error parameter first
    parameter('error)(redirectToFrontendWithError) ~
      // actual callback
      parameters('code → 'state)(callback) ~
      // fallback when code/state/error are not provided
      redirectToFrontendWithError("No data provided")

  val route: Route = (get & pathPrefix("register")) {
    pathEndOrSingleSlash(startRedditOauthFlow) ~
      path("callback")(handleRedditCallback)
  }
}
