package reddit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{ExecutionContext, Future}

/**
  * Makes authorized calls to reddit at oauth.reddit.com
  */
class RedditSecuredApiConsumer(config: RedditConfig)(implicit system: ActorSystem, ec: ExecutionContext)
    extends ApiConsumer("oauth.reddit.com", 10) {
  import RedditApiJsonSupport._

  def getUsername(accessToken: String): Future[String] = {
    val request = HttpRequest(
      uri = s"/api/v1/me",
      method = HttpMethods.GET,
      headers = Authorization(OAuth2BearerToken(accessToken)) :: Nil
    )

    system.log.debug(s"Fetching username using access token $accessToken, request $request")

    for {
      response ← queueRequest(request)
      if response.status == StatusCodes.OK
      parsed ← Unmarshal(response).to[MeResponse]
    } yield parsed.name
  }
}
