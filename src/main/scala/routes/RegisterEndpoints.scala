package routes

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akkahttptwirl.TwirlSupport
import com.softwaremill.session.{SessionConfig, SessionManager}
import reddit.{RedditAuthenticationApiConsumer, RedditAuthenticationException, RedditConfig, RedditSecuredApiConsumer}
import services.{DatabaseService, UserService}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import validation.Emails

import scala.concurrent.ExecutionContext
import scala.util.Success

object RegistrationProtocol extends DefaultJsonProtocol {
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
      password.length >= 8,
      "Password must contain at least 8 characters"
    )
    require(
      Emails.isValid(email),
      "Invalid email provided"
    )
  }

  implicit val registerRequestParser: RootJsonFormat[RegisterRequest] = jsonFormat3(RegisterRequest)
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
  val startRedditOauthFlow: Route = (get & pathEndOrSingleSlash) {
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
  val handleRedditCallback: Route = (get & path("callback")) {
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
  }

  val finaliseForm: Route = (get & path("complete")) {
    requiredSession(oneOff, usingCookies) { session: Map[String, String] ⇒
      session.get("username") match {
        case Some(username) ⇒
          onComplete(databaseService.run(userService.isUsernameInUse(username))) {
            case Success(false) ⇒ // Username not in use
              complete(html.react("register"))
            case _ ⇒
              complete(html.registerError("Username is already registered")) // TODO login automatically instead?
          }
        case _ ⇒
          redirect("/register", StatusCodes.TemporaryRedirect) // redirect to start of the flow, we have no username in session
      }
    }
  }

  val finaliseFormSubmit: Route = (post & path("complete") & entity(as[RegisterRequest])) { request ⇒
    requiredSession(oneOff, usingCookies) { session: Map[String, String] ⇒
      session.get("username") match {
        case Some(username) ⇒
          // TODO insert new row
          complete(s"$request $username")
        case _ ⇒
          complete(StatusCodes.Unauthorized)
      }
    }
  }

  val routes: Route = pathPrefix("/register") {
    startRedditOauthFlow ~ handleRedditCallback ~ finaliseForm ~ finaliseFormSubmit
  }
}
