import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

class SwaggerDocRoutes(implicit val actorSystem: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  implicit val materializer = ActorMaterializer()
  override val apiTypes     = Seq()
  override val host         = "localhost:8000"
  override val basePath     = "/"
  override val apiDocsPath  = "docs"
}
