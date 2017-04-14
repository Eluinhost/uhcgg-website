import akka.http.scaladsl.server.Route

class ApiService() {
  import akka.http.scaladsl.server.Directives._

  val routes: Route = pathPrefix("v1") {
    complete("TODO")
  }
}
