scalaVersion := "2.11.7"

name := "atom-flow"

version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.twitter" %% "scrooge-core" % "3.17.0",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "com.amazonaws" % "aws-java-sdk-kinesis" % "1.10.42",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.slf4j" % "slf4j-simple" % "1.7.13"
)

//   "com.amazonaws" % "amazon-kinesis-client" % "1.2.1",
//   "com.typesafe.akka" %% "akka-actor" % "2.4.1"
