package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akkahttptwirl.TwirlSupport
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

class SwaggerDocumentationEndpoints(implicit val actorSystem: ActorSystem)
    extends SwaggerHttpService
    with HasActorSystem
    with HasRoutes
    with TwirlSupport {
  implicit val materializer = ActorMaterializer()
  override val apiTypes     = Seq()
  override val host         = "localhost:8000"
  override val basePath     = "/"
  override val apiDocsPath  = "docs"

  override lazy val routes: Route =
    path(apiDocsBase / "swagger.json") {
      get {
        complete(HttpEntity(MediaTypes.`application/json`, generateSwaggerJson))
      }
    } ~ (path(apiDocsBase) & pathEndOrSingleSlash) {
      complete(html.docs.render())
    }
}
