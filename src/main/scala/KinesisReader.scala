package kinesisreader

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.GetRecordsRequest
import com.amazonaws.services.kinesis.model.Record
import com.typesafe.config.Config
import java.util.{ UUID, List => JList }
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class KinesisReader(appConfig: Config) {


  private lazy val credentials = new BasicAWSCredentials(
    appConfig.getString("kinesis.accessKey"),
    appConfig.getString("kinesis.secretKey")
  )

  lazy val region = Region.getRegion(
    Regions.fromName(appConfig.getString("kinesis.region"))
  )

  private lazy val kinesisClient = {
    val c = new AmazonKinesisClient(credentials)
    c.setRegion(region)
    c
  }

  val appName = appConfig.getString("kinesis.appname")

  val streamName: String = appConfig.getString("kinesis.streamName")

  //private val workerId = UUID.randomUUID().toString





  // def start: Unit = {
  //   println("PMR start")
  //   workerThread.start
  //   println("PMR sleep")
  //   Thread.sleep(1000)
  //   println("PMR stop")
  //   worker.shutdown
  // }

  def shardIterator = {
    val streamInfo = kinesisClient.describeStream(streamName)
    val shard = streamInfo.getStreamDescription().getShards.toList.head
    kinesisClient.getShardIterator(streamName, shard.getShardId, "TRIM_HORIZON").getShardIterator
  }

  // def getStreams: List[String] =
  //   kinesisClient.listStreams().getStreamNames().toList

  def getRecords = {
    kinesisClient.getRecords(new GetRecordsRequest().withShardIterator(shardIterator)).getRecords().toList
  }
}

// object KinesisReader {
//   def apply(config: Config)(handler: (Record) => Unit) = new KinesisReader(config, handler)
// }
