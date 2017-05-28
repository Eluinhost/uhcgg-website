package gg.uhc.website.database

import akka.actor.ActorSystem
import doobie.imports.{ConnectionIO, Transactor}

import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

/**
  * Designed to run all transactions on a specific actor system
  */
class DatabaseRunner(transactor: Transactor[Task]) {
  val system      = ActorSystem("database-access")
  implicit val ec = system.dispatcher

  def run[A](connectionIO: ConnectionIO[A]): Future[A] = {
    val promise = Promise[A]()

    transactor
      .trans(connectionIO)
      .unsafePerformAsync {
        case -\/(t) ⇒ promise failure t
        case \/-(v) ⇒ promise success v
      }

    promise.future
  }

  def apply[A](connectionIO: ConnectionIO[A]): Future[A] = run(connectionIO)
}
