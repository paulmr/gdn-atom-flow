package kinesisreader

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason
import com.amazonaws.services.kinesis.model.GetRecordsRequest
import com.amazonaws.services.kinesis.model.Record
import com.typesafe.config.Config
import java.util.{ UUID, List => JList }
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{
  Worker,
  KinesisClientLibConfiguration,
  InitialPositionInStream
}
import net.ceedubs.ficus.Ficus._

class KinesisReader(appConfig: Config) {

  class EventProcessor extends IRecordProcessor {
    def initialize(shardId: String) = {
      println(s"SHARDID: $shardId")
    }
    def processRecords(records: JList[Record], checkpointer: IRecordProcessorCheckpointer) = {
      println(s"Records: ${records.size()}")
    }
    def shutdown(checkpointer: IRecordProcessorCheckpointer, reason: ShutdownReason) = {
      println(s"Shutdown $reason")
    }
  }

  val appName = appConfig.getAs[String]("kinesis.appname").get

  val streamName: String = appConfig.getAs[String]("kinesis.streamName").get

  private lazy val credentialsProvider = new DefaultAWSCredentialsProviderChain

  private val workerId = UUID.randomUUID().toString

  /* only applies when there are no checkpoints */
  val initialPosition = InitialPositionInStream.TRIM_HORIZON

  private lazy val config =
    new KinesisClientLibConfiguration(appName, streamName, credentialsProvider, workerId)
      .withInitialPositionInStream(initialPosition)
      .withRegionName(appConfig.getAs[String]("kinesis.region").getOrElse("eu-west-1"))

  private lazy val eventProcessorFactory = new IRecordProcessorFactory {
    def createProcessor(): IRecordProcessor = new EventProcessor
  }

  /* Create a worker, which will in turn create one or more EventProcessors */
  lazy val worker = new Worker(
    eventProcessorFactory,
    config
  )

  // def shardIterator = {
  //   val streamInfo = kinesisClient.describeStream(streamName)
  //   val shard = streamInfo.getStreamDescription().getShards.toList.head
  //   kinesisClient.getShardIterator(streamName, shard.getShardId, "TRIM_HORIZON").getShardIterator
  // }

  // def getStreams: List[String] =
  //   kinesisClient.listStreams().getStreamNames().toList

  // def getRecords: List[Record] =
  //   kinesisClient.getRecords(new GetRecordsRequest().withShardIterator(shardIterator)).getRecords().toList

}
