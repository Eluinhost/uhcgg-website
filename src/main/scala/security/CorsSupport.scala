package security

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive0, Route}
import com.typesafe.config.ConfigFactory

trait CorsSupport {
  import akka.http.scaladsl.model.HttpMethods._
  import akka.http.scaladsl.model.headers._
  import akka.http.scaladsl.server.Directives._

  val defaultOrigin: `Access-Control-Allow-Origin` = ConfigFactory.load().getString("cors.allowed-origin")

  implicit def stringToAccessControlOrigin(s: String): `Access-Control-Allow-Origin` = {
    case "*" ⇒ `Access-Control-Allow-Origin`.*
    case _ ⇒ `Access-Control-Allow-Origin`(HttpOrigin(s))
  }

  private def addAccessControlHeaders(origin: `Access-Control-Allow-Origin`): Directive0 = {
    mapResponseHeaders { headers ⇒
      origin +:
        `Access-Control-Allow-Credentials`(true) +:
        `Access-Control-Allow-Headers`("Token", "Content-Type", "X-Requested-With") +:
        headers
    }
  }

  private def preflightRequestHandler: Route = options {
    respondWithHeader(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)) {
      complete(StatusCodes.OK)
    }
  }

  def corsHandler(origin: `Access-Control-Allow-Origin`)(r: ⇒ Route): Route = addAccessControlHeaders(origin) {
    preflightRequestHandler ~ r
  }

  def corsHandler(r: ⇒ Route): Route = corsHandler(defaultOrigin)(r)
}
