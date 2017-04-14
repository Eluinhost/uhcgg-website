package reddit

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object RedditApiJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val accessTokenResponseFormat: RootJsonFormat[AccessTokenResponse] = jsonFormat4(AccessTokenResponse)
  implicit val meResponseFormat: RootJsonFormat[MeResponse]                   = jsonFormat1(MeResponse)
}
