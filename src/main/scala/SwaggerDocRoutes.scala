import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

class SwaggerDocRoutes(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer = ActorMaterializer()
  override val apiTypes = Seq()
  override val host = "localhost:8000"
  override val basePath = "/"
  override val apiDocsPath = "/docs"
}
