scalaVersion := "2.11.7"

name := "atom-flow"

version := "1.0.0-SNAPSHOT"

resolvers += Resolver.jcenterRepo // for ficus

libraryDependencies ++= Seq(
  "com.twitter" %% "scrooge-core" % "3.17.0",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "com.amazonaws" % "aws-java-sdk-kinesis" % "1.9.29",
  "com.amazonaws" % "amazon-kinesis-client" % "1.2.1",
  "com.typesafe" % "config" % "1.2.1",
  "com.iheart" %% "ficus" % "1.1.3"
)