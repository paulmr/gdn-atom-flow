package atomflow

import akka.pattern.ask
import atomflow.store._
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props, ActorRef }
import akka.io.IO
import spray.can.Http
import akka.util.Timeout
import scala.concurrent.duration._

import api.v1._

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()

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

  val apiHandler = system.actorOf(Props(classOf[ApiHandler[Int]], store))

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? (Http.Bind(apiHandler, interface = "localhost", port = 8980))

}
