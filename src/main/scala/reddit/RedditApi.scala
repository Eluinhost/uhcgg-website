package reddit

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RedditApi(config: RedditConfig)(implicit system: ActorSystem, ec: ExecutionContext) {
  import RedditApiJsonSupport._

  implicit val materializer = ActorMaterializer()

  // Create the client header used when querying for access tokens
  val clientCredentialsHeader = Authorization(
    credentials = BasicHttpCredentials(
      username = config.clientId,
      password = config.clientSecret
    )
  )

  // Shared connection pool for all requests
  val accessTokenPool = Http().cachedHostConnectionPoolHttps[NotUsed]("www.reddit.com")
  val oauthPool       = Http().cachedHostConnectionPoolHttps[NotUsed]("oauth.reddit.com")

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

    // Send request and read response
    Source
      .single(request → NotUsed)
      .via(accessTokenPool)
      .runWith(Sink.head)
      .flatMap { // Convert try into successful/failure
        case (Success(r: HttpResponse), _) ⇒
          if (r.status == StatusCodes.OK) {
            system.log.debug(s"Got valid response for access token request $r")
            Future successful r
          } else {
            system.log.error(s"Failed to fetch access token, status code ${r.status}, response $r")
            Future failed new IllegalArgumentException("Invalid response from Reddit")
          }
        case (Failure(f), _) ⇒
          system.log.error(f, "Failed to fetch access token")
          Future failed f
      }
      .flatMap(Unmarshal(_).to[AccessTokenResponse]) // Try to convert response back
      .map(_.access_token)
  }

  def queryUsername(accessToken: String): Future[String] = {
    val request = HttpRequest(
      uri = s"/api/v1/me",
      method = HttpMethods.GET,
      headers = Authorization(OAuth2BearerToken(accessToken)) :: Nil
    )

    system.log.debug(s"Fetching username using access token $accessToken, request $request")

    Source
      .single(request → NotUsed)
      .via(oauthPool)
      .runWith(Sink.head)
      .flatMap {
        case (Success(r: HttpResponse), _) ⇒
          if (r.status == StatusCodes.OK) {
            system.log.debug(s"Got valid response for username request $r")
            Future successful r
          } else {
            system.log.error(s"Failed to fetch username, status code ${r.status}, response $r")
            Future failed new IllegalArgumentException("Invalid response from Reddit")
          }
        case (Failure(f), _) ⇒
          system.log.error(f, "Failed to fetch username")
          Future failed f
      }
      .flatMap(Unmarshal(_).to[MeResponse])
      .map(_.name)
  }
}
