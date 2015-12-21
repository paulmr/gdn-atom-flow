package kinesisreader

import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS
/* simple cli tool */

object Main extends App {

  val config = ConfigFactory.load()
  val kinesis = new KinesisReader(config)

  println(s"Connecting to: ${kinesis.streamName}")

  def go(count: Int = 0, max: Int = 10, delay: Duration = Duration(1, SECONDS)): Unit = {
    val records = kinesis.getRecords
    println(s"[$count] Got ${records.length}")
    Thread.sleep(delay.toMillis)
    if((count + 1) < max) go(count + 1, max, delay)
  }

  go()
}
