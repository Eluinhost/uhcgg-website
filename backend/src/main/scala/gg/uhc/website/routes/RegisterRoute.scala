package gg.uhc.website.routes

import java.net.URLEncoder
import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akkahttptwirl.TwirlSupport
import com.softwaremill.tagging.@@
import gg.uhc.website.CustomJsonCodec
import gg.uhc.website.configuration.JwtSecret
import gg.uhc.website.helpers.reddit.{RedditAuthenticationApi, RedditSecuredApi}
import gg.uhc.website.repositories.UserRepository
import gg.uhc.website.validation.Emails
import io.circe.parser._
import io.circe.syntax._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}
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

/**
  * @param username set to None if not authorised yet
  */
case class RegistrationSession(username: Option[String], randomState: String)

class RegisterRoute(
    userRepository: UserRepository,
    redditAuthenticationApi: RedditAuthenticationApi,
    redditSecuredApi: RedditSecuredApi,
    jwtSecret: String @@ JwtSecret,
    jwtAlgorithm: JwtHmacAlgorithm)
    extends PartialRoute
    with TwirlSupport
    with CustomJsonCodec {

  /**
    * Redirects off to helpers.reddit for authorization
    */
  def startRedditOauthFlow: Route = redirect(
    redditAuthenticationApi.startAuthFlowUrl(generateToken(username = None)),
    StatusCodes.TemporaryRedirect
  )

  private def generateToken(username: Option[String]): String = {
    val now = Instant.now()

    // used to improve entropy in generated token, we don't actually use/check it though so probably not even worth it
    val randomState = UUID.randomUUID().toString

    val claim = JwtClaim(
      content = RegistrationSession(username, randomState).asJson.noSpaces,
      expiration = Some(now.plusSeconds(5 * 60).getEpochSecond),
      issuedAt = Some(now.getEpochSecond)
    )

    JwtCirce.encode(claim, jwtSecret, jwtAlgorithm)
  }

  private def parseToken(token: String): Option[RegistrationSession] =
    (for {
      claim   ← JwtCirce.decode(token, jwtSecret, Seq(jwtAlgorithm)).toEither.right
      json    ← parse(claim.content).right
      session ← json.as[RegistrationSession].right
    } yield session).toOption

  def redirectToFrontend(username: String): Route = redirect(
    s"/register#${generateToken(username = Some(username))}",
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
    parseToken(state) match {
      case None ⇒ redirectToFrontendWithError("Invalid token supplied")
      case Some(_) ⇒
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

  def registerFormSubmit(request: RegisterRequest): Route = extractClientIP { ip ⇒
    // check valid token + username exists
    val maybeUsername = for {
      session  ← parseToken(request.token)
      username ← session.username
    } yield username

    if (maybeUsername.isEmpty)
      return complete(StatusCodes.BadRequest → "Invalid token")

    // create the new user
    val task = userRepository.createUser(maybeUsername.get, request.email, request.password)

    onComplete(task) {
      case Success(_) ⇒
        complete(StatusCodes.Created)
      case Failure(ex) ⇒
        extractLog { logger ⇒
          logger.error(ex, "Failed to add account")
          complete(StatusCodes.InternalServerError)
        }
    }
  }

  val route: Route = pathPrefix("register") {
    get {
      pathEndOrSingleSlash(startRedditOauthFlow) ~
        path("callback")(handleRedditCallback)
    } ~ (post & pathEndOrSingleSlash) {
      entity(as[RegisterRequest])(registerFormSubmit)
    }
  }
}
