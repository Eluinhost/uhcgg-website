package reddit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy, QueueOfferResult}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class ApiConsumer(host: String, queueSize: Int)(implicit actorSystem: ActorSystem) {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private[this] val pool = Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](host)

  private[this] val queue = Source
    .queue[(HttpRequest, Promise[HttpResponse])](queueSize, OverflowStrategy.dropNew)
    .via(pool)
    .toMat(Sink.foreach({
      case ((Success(response), promise))  ⇒ promise.success(response)
      case ((Failure(exception), promise)) ⇒ promise.failure(exception)
    }))(Keep.left)
    .run()

  protected def queueRequest(request: HttpRequest): Future[HttpResponse] = {
    val promise = Promise[HttpResponse]()

    queue
      .offer(request → promise)
      .flatMap {
        case QueueOfferResult.Enqueued    ⇒ promise.future
        case QueueOfferResult.Dropped     ⇒ Future failed new RuntimeException("Queue overflowed")
        case QueueOfferResult.Failure(ex) ⇒ Future failed ex
        case QueueOfferResult.QueueClosed ⇒ Future failed new RuntimeException("Queue closed")
      }
  }
}
