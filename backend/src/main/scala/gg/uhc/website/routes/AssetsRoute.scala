package gg.uhc.website.routes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{RejectionHandler, Route}

class AssetsRoute extends PartialRoute {
  // Rejection handler for not found assets, simple 404 response code, no data
  val rejectionHandler = RejectionHandler.newBuilder
    .handleNotFound {
      complete(StatusCodes.NotFound)
    }
    .result()

  override def route: Route = pathPrefix("assets" / Remaining) { file ⇒
    (encodeResponse & handleRejections(rejectionHandler)) {
      getFromResource("public/" + file)
    }
  }
}
