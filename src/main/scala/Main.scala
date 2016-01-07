package atomflow

import scala.io.Source
import java.io.{ FileWriter, File }
import java.nio.ByteBuffer
import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, SECONDS }
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.gu.contentatom.thrift

import scala.concurrent.ExecutionContext.Implicits.global

/* simple cli tool */

object Main extends App with RecordProcessor with LazyLogging {
  val config = ConfigFactory.load()
  val kinesis = new KinesisReader(config)

  val timeout = Duration(config.getLong("kinesis.timeout"), SECONDS)

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

  logger.info(s"Connecting to: ${kinesis.streamName}")

  val startIt = readCheckpointFile().map(kinesis.newIteratorFrom(_)) getOrElse kinesis.newIteratorAll()

  val futureRecords = kinesis.waitForRecords(Duration(5, "seconds"), startIt)

  logger.info("Waiting for records ...")

  val res = Await.result(futureRecords, timeout)

  res.records.foreach(processRecord _)

  // if we have a new sequence number, write it to the checkpoint
  res.sequenceNum.foreach(writeCheckpoint _)
}
