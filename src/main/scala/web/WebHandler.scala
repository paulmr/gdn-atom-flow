package atomflow.web

import spray.routing.{ HttpServiceActor, HttpService }
import spray.routing.Route
import spray.httpx.PlayTwirlSupport
import views.html.app._
import atomflow.store.Store

class WebHandler[Id](store: Store[Id]) extends HttpServiceActor with PlayTwirlSupport {
  val route = path("last") {
    complete(last(store.tail(5)))
  } ~ pathPrefix("static") {
    getFromResourceDirectory("web")
  }


  def receive = runRoute(route)
}
