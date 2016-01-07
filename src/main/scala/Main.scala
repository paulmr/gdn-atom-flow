package atomflow

import atomflow.store._
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()

  System.setProperty(
    "org.slf4j.simpleLogger.logFile", config.getString("log.file")
  )

  System.setProperty(
    "org.slf4j.simpleLogger.defaultLogLevel", config.getString("log.level")
  )

  println("log level: " + System.getProperty("org.slf4j.simpleLogger.defaultLogLevel"))

  val store = new MemStore()

  val atomFlowThread = new AtomFlow(config, store)

  atomFlowThread.start

  /* XXX - DEBUG */
  Thread.sleep(20000)
  val contents = store.tail(10)
  println(s"PMR store contains ${contents.length} item(s)")
  /* XXX - END DEBUG */
}
