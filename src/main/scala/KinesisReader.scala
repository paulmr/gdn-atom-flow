package atomflow

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.{
  GetRecordsRequest, GetShardIteratorRequest, Record
}
import com.typesafe.config.Config
import java.util.{ UUID, List => JList }
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import scala.collection.JavaConversions._
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration
import java.nio.ByteBuffer

class KinesisReader(appConfig: Config) {

  case class GetRecordsResponse(
    nextIterator: String,
    sequenceNum: Option[String], // will be None if no records returned
    records: List[Record]
  )

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

  private def requestIterator(startReq: GetShardIteratorRequest) = {
    val streamInfo = kinesisClient.describeStream(streamName)
    val shard = streamInfo.getStreamDescription().getShards.toList.head
    val req = startReq
        .withStreamName(streamName)
        .withShardId(shard.getShardId)
    kinesisClient.getShardIterator(req).getShardIterator
  }

  def newIteratorAll(): String = requestIterator(
    new GetShardIteratorRequest().withShardIteratorType("TRIM_HORIZON")
  )

  def newIteratorLatest(): String = requestIterator(
    new GetShardIteratorRequest().withShardIteratorType("LATEST")
  )

  def newIteratorFrom(startSeq: String): String = requestIterator(
    new GetShardIteratorRequest()
      .withShardIteratorType("AFTER_SEQUENCE_NUMBER")
      .withStartingSequenceNumber(startSeq)
  )

  def getRecords(it: String): GetRecordsResponse = {
    val res = kinesisClient.getRecords(new GetRecordsRequest().withShardIterator(it))
    val recs = res.getRecords().toList
    GetRecordsResponse(
      res.getNextShardIterator(),
      recs.lastOption.map(_.getSequenceNumber()),
      recs
    )
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
