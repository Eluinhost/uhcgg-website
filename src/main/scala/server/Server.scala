package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.softwaremill.tagging.@@
import configuration.{ServerHostConfig, ServerPortConfig}
import routes.BaseRoute

import scala.concurrent.{Await, ExecutionContext, Future}

class Server(baseRoute: BaseRoute, host: String @@ ServerHostConfig, port: Int @@ ServerPortConfig) {
  import scala.concurrent.duration._

  implicit val system               = ActorSystem("http-actor-system")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer         = ActorMaterializer()

  def bind(): Future[ServerBinding] = Http().bindAndHandle(baseRoute.route, host, port)

  def afterStart(binding: ServerBinding): Unit = system.log.info(s"Server started on ${binding.localAddress.toString}")

  def beforeStop(binding: ServerBinding): Unit = {
    system.log.info("Shutting down")

    Await.ready(for {
      _ ← binding.unbind()
      _ = system.log.info("Unbound server")
      t ← system.terminate()
      _ = system.log.info("Shut down actor system")
    } yield t, 1 minute)
  }
}
