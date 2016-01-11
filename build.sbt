scalaVersion := "2.11.7"

name := "atom-flow"

version := "1.0.0-SNAPSHOT"

lazy val sprayVersion = "1.3.3"
lazy val json4sVersion = "3.3.0"

libraryDependencies ++= Seq(
  "com.twitter" %% "scrooge-core" % "3.17.0",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "com.amazonaws" % "aws-java-sdk-kinesis" % "1.10.42",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "com.twitter" %% "scrooge-core" % "3.17.0",
  "com.gu" %% "content-atom-model-scala" % "0.2.1",
  "com.gu" %% "content-atom-util" % "0.2.1-SNAPSHOT",
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-http" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "org.json4s" %% "json4s-core" % json4sVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "com.typesafe.akka" %% "akka-actor" % "2.3.14",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

//   "com.amazonaws" % "amazon-kinesis-client" % "1.2.1",
