package atomflow.model

import java.util.Date

case class Event(
  atomId: String,
  eventType: String,
  startDate: Date,
  status: EventStatus.Value
)

object Event {
  implicit val ordering = new Ordering[Event] {
    def compare(a: Event, b: Event) = {
      val lorder = implicitly[Ordering[Long]]
      lorder.compare(a.startDate.getTime(), b.startDate.getTime())
    }
  }
}

object EventStatus extends Enumeration {
  val OPEN      = Value
  val COMPLETED = Value
}
