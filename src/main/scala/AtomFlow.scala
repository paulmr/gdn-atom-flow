package atomflow

import scala.io.Source
import java.io.{ FileWriter, File }
import java.nio.ByteBuffer
import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, SECONDS }
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.gu.contentatom.thrift
import atomflow.store._

import scala.concurrent.ExecutionContext.Implicits.global

/* simple cli tool */

class AtomFlow[Id](config: Config, val store: Store[Id]) extends Thread
    with RecordProcessor
    with LazyLogging {

  val kinesis = new KinesisReader(config)

  private var active = true

  lazy val checkpointFile = {
    if(config.hasPath("checkpointfile"))
      Some(new File(config.getString("checkpointfile")))
    else
      None
  }

  def readCheckpointFile(): Option[String] = checkpointFile flatMap { file =>
    if(file.exists()) {
      Some(Source.fromFile(file).mkString)
    } else {
      logger.warn(s"Checkpoint file $file doesn't exist")
      None
    }
  }

  def writeCheckpoint(sq: String): Unit = checkpointFile foreach { file =>
    val out = new FileWriter(file)
    out.write(sq, 0, sq.length)
    out.close()
    logger.info(s"Wrote checkpoint to $file")
  }


  override def run = {
    logger.info(s"Connecting to: ${kinesis.streamName}")

    var iterator =
      readCheckpointFile().map(kinesis.newIteratorFrom(_)) getOrElse kinesis.newIteratorAll()

    while(active) {
      val res = kinesis.getRecords(iterator)
      res.records.foreach(processRecord _)
      // if we have a new sequence number, write it to the checkpoint
      res.sequenceNum.foreach(writeCheckpoint _)
      // delay before we try again
      if(active) {
        val seconds = 5
        logger.info(s"Sleeping for $seconds seconds")
        Thread.sleep(seconds * 1000)
      }
      iterator = res.nextIterator
    }
  }

  def shutdown = {
    active = false
  }
}
