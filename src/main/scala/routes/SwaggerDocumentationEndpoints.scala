package routes

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

class SwaggerDocumentationEndpoints(implicit val actorSystem: ActorSystem)
    extends SwaggerHttpService
    with HasActorSystem
    with HasRoutes {
  implicit val materializer = ActorMaterializer()
  override val apiTypes     = Seq()
  override val host         = "localhost:8000"
  override val basePath     = "/"
  override val apiDocsPath  = "docs"
}
