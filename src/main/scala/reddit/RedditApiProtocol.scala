package reddit

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object RedditApiProtocol extends DefaultJsonProtocol {
  case class MeResponse(name: String)
  case class AccessTokenResponse(access_token: String, token_type: String, expires_in: Int, scope: String)

  implicit val accessTokenResponseFormat: RootJsonFormat[AccessTokenResponse] = jsonFormat4(AccessTokenResponse)
  implicit val meResponseFormat: RootJsonFormat[MeResponse]                   = jsonFormat1(MeResponse)
}
