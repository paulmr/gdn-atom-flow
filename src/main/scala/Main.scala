package kinesisreader

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global

/* simple cli tool */

object Main extends App with LazyLogging {

  val config = ConfigFactory.load()
  val kinesis = new KinesisReader(config)

  logger.info(s"Connecting to: ${kinesis.streamName}")

  val startIt = kinesis.newIterator(kinesis.IteratorType.All)

  val futureRecords = kinesis.waitForRecords(Duration(5, "seconds"), startIt)

  logger.info("Waiting for records ...")

  val res = Await.result(futureRecords, Duration.Inf)

  res.records foreach { record =>
    logger.info(s"Record: sent ${record.getApproximateArrivalTimestamp}")
  }

}
