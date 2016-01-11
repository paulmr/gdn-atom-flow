package atomflow.api.v1

import atomflow.store.Store
import spray.routing.{ HttpServiceActor, HttpService }
import spray.routing.Route

class ApiHandler[Id](val store: Store[Id]) extends HttpServiceActor with ApiService {

  def receive = runRoute(route)

}

trait ApiService extends HttpService {
  val store: Store[_]
  val route: Route = pathPrefix("api") {
    path("last") {
      get {
        parameter("count" ? "10") { count =>
          val events = store.tail(count.toInt)
          complete(s"Events: ${events.length} of $count")
        }
      }
    }
  }
}
