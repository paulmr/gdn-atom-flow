package atomflow.api.v1

import atomflow.store.Store
import spray.routing.{ HttpServiceActor, HttpService }
import spray.routing.Route
import org.json4s.{ JString, CustomSerializer }
import spray.httpx.Json4sJacksonSupport
import atomflow.model.EventStatus

class ApiHandler[Id](val store: Store[Id]) extends HttpServiceActor with ApiService {
  def receive = runRoute(route)
}

trait ApiService extends HttpService with Json4sJacksonSupport {

  val statusSerializer = new CustomSerializer[EventStatus.Value](
    (formats) => (
      { case JString(s) => EventStatus.withName(s) },
      { case status: EventStatus.Value => JString(status.toString) }
    )
  )

  implicit def json4sJacksonFormats = org.json4s.DefaultFormats + statusSerializer

  val store: Store[_]
  val route: Route = pathPrefix("api") {
    path("last") {
      get {
        parameter("count" ? "10") { count =>
          val events = store.tail(count.toInt)
          complete(events)
        }
      }
    }
  }
}
