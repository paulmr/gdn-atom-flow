package atomflow.model

import java.util.Date

case class Event(
  atomId: String,
  eventType: String,
  startDate: Date,
  status: EventStatus.Value
)

object EventStatus extends Enumeration {
  val OPEN      = Value
  val COMPLETED = Value
}
