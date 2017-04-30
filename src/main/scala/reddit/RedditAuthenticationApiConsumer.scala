package reddit

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.Future

class RedditAuthenticationApiConsumer(config: RedditConfig)(implicit system: ActorSystem)
    extends ApiConsumer("www.reddit.com", 10)
    with FailFastCirceSupport {
  import io.circe.generic.auto._

  // Create the client header used when querying for access tokens
  val clientCredentialsHeader = Authorization(
    credentials = BasicHttpCredentials(
      username = config.clientId,
      password = config.clientSecret
    )
  )

  def getAccessToken(authCode: String): Future[String] = {
    // build the request
    val request = HttpRequest(
      uri = s"/api/v1/access_token",
      method = HttpMethods.POST,
      headers = clientCredentialsHeader :: Nil,
      entity = FormData(
        "code"         → authCode,
        "grant_type"   → "authorization_code",
        "redirect_uri" → config.redirectUri
      ).toEntity
    )

    system.log.debug(s"Fetching access token from auth code $authCode, request $request")

    for {
      response ← queueRequest(request)
      if response.status == StatusCodes.OK
      parsed ← Unmarshal(response).to[AccessTokenResponse]
    } yield parsed.access_token
  }
}
