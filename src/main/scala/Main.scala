package kinesisreader

import com.typesafe.config.ConfigFactory

/* simple cli tool */

object Main extends App {

  val config = ConfigFactory.load()
  val kinesis = new KinesisReader(config)

}
