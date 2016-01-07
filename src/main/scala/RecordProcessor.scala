package atomflow

import com.amazonaws.services.kinesis.model.Record
import com.gu.contentatom.thrift.ContentAtomEvent
import com.typesafe.scalalogging.LazyLogging
import java.util.Date
import scala.util.Try
import scala.util.{ Failure, Success }
import atomflow.store.Store
import atomflow.model._

trait RecordProcessor extends LazyLogging {

  val store: Store[_]

  def logMessage(arrivalTime: Date, maybeDeserialised: Try[ContentAtomEvent]) = {
    val logMessage = maybeDeserialised map { atomEvent =>
      val atom = atomEvent.atom
      s"<Record> sent: $arrivalTime, for id: ${atom.atomType}/${atom.id}"
    }
    logMessage match {
      case Success(msg) => logger info msg
      case Failure(err) => logger error s"Unsuccessful deserialization (arrived: $arrivalTime): ${err}"
    }
  }

  def putEvent(arrivalTime: Date, wireEv: ContentAtomEvent) = {
    val ev = Event(
      atomId = wireEv.atom.id,
      eventType = wireEv.eventType.name,
      startDate = arrivalTime,
      status = EventStatus.OPEN
    )
    val id = store.putEvent(ev)
  }

  def processRecord(record: Record): Unit = {
    val data = record.getData
    val deserialized = AtomDeserializer.deserialize(data)
    val arrivalDate = record.getApproximateArrivalTimestamp
    logMessage(arrivalDate, deserialized)
    deserialized foreach { ev => putEvent(arrivalDate, ev) }
  }
}
