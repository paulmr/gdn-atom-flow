package atomflow

import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()

  System.setProperty(
    "org.slf4j.simpleLogger.logFile", config.getString("config.file")
  )

  System.setProperty(
    "org.slf4j.simpleLogger.defaultLogLevel", config.getString("config.level")
  )

  println("log level: " + System.getProperty("org.slf4j.simpleLogger.defaultLogLevel"))

  val atomFlowThread = new AtomFlow(config)

  atomFlowThread.start
}
