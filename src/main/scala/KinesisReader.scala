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
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration
import java.nio.ByteBuffer

class KinesisReader(appConfig: Config) {

  sealed trait IteratorType { val value: String }
  object IteratorType {
    object Latest extends IteratorType { val value = "LATEST" }
    object All extends IteratorType    { val value = "TRIM_HORIZON" }
  }

  case class GetRecordsResponse(nextIterator: String, records: List[Record])

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

  def newIterator(iteratorType: IteratorType): String = {
    val streamInfo = kinesisClient.describeStream(streamName)
    val shard = streamInfo.getStreamDescription().getShards.toList.head
    kinesisClient.getShardIterator(streamName, shard.getShardId, iteratorType.value).getShardIterator
  }

  def getRecords(it: String): GetRecordsResponse = {
    val res = kinesisClient.getRecords(new GetRecordsRequest().withShardIterator(it))
    GetRecordsResponse(res.getNextShardIterator(), res.getRecords().toList)
  }

  def waitForRecords(delay: Duration, it: String)(implicit ec: ExecutionContext):
      Future[GetRecordsResponse] = {
    @annotation.tailrec
    def tryToGetRecords: GetRecordsResponse = {
      val response = getRecords(it)
      if(response.records.length > 0) {
        response
      } else {
        // sleep and try again
        Thread.sleep(delay.toMillis)
        tryToGetRecords
      }
    }

    Future(tryToGetRecords)
  }
}
