package atomflow

import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()

  System.setProperty(
    "org.slf4j.simpleLogger.logFile", config.getString("configFile")
  )

  val atomFlowThread = new AtomFlow(config)

  atomFlowThread.start
}
