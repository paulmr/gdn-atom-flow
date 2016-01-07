package atomflow

import com.amazonaws.services.kinesis.model.Record
import com.typesafe.scalalogging.LazyLogging
import scala.util.{ Failure, Success }

trait RecordProcessor extends LazyLogging {
  def processRecord(record: Record): Unit = {
    val data = record.getData
    val logMessage = AtomDeserializer.deserialize(data) map { atomEvent =>
      val atom = atomEvent.atom
      s"<Record> sent: ${record.getApproximateArrivalTimestamp}, for id: ${atom.atomType}/${atom.id}"
    }
    logMessage match {
      case Success(msg) => logger info msg
      case Failure(err) => logger error s"Unsuccessful deserialization: ${err}"
    }
  }
}
