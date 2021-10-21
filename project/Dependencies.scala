import sbt._

object Dependencies extends Akka {

  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.32"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.6"
  val logbackLogstash = "net.logstash.logback" % "logstash-logback-encoder" % "6.6"
  val scribe = "com.outr" %% "scribe" % "3.5.5"
  val scribeJson = "com.outr" %% "scribe-json" % "3.5.5"
  val scribeSlf4j = "com.outr" %% "scribe-slf4j" % "3.0.2"

  //lazy val algolia = "3.14.1"
  //"com.algolia" % "algoliasearch-core" % algolia,
  //"com.algolia" % "algoliasearch-java-net" % algolia,

  lazy val scalatestVersion = "3.2.10"
  val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion % Test
  val scopt = "com.github.scopt" %% "scopt" % "4.0.1"
  val gcFunctions = "com.google.cloud.functions" % "functions-framework-api" % "1.0.4" % Provided
  val telegramBot = "org.telegram" % "telegrambots" % "5.3.0"
  val sconfig = "org.ekrich" %% "sconfig" % "1.4.4"
  val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.17.0"
  val cdktf = "com.hashicorp" % "cdktf" % "0.6.4"
  val constructs = "software.constructs" % "constructs" % "10.0.5"
}

trait Akka {

  lazy val akkaVersion = "2.6.16"
  lazy val akkaHttpVersion = "10.2.6"
  lazy val akkaHttpCorsVersion = "1.1.2"
  lazy val akkaGrpcVersion = "2.0.0"
  lazy val akkaManagementVersion = "1.1.1"
  lazy val alpakkaKafkaVersion = "2.1.1"
  lazy val kamonVersion = "2.2.3"

  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % akkaVersion
  val akkaPersistenceTyped = "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion
  val akkaSerializationJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion
  val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.5"
  val akkaLevelDBDependency = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  //val akkaPersistenceCouchbase = "com.lightbend.akka" % "akka-persistence-couchbase" % "1.0"
  val akkaStreamKafka = "com.typesafe.akka" %% "akka-stream-kafka" % alpakkaKafkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  //"com.typesafe.akka" %% "akka-pki" % akkaVersion,
  val akkaHttpCors = "ch.megard" %% "akka-http-cors" % akkaHttpCorsVersion // Para poder usar akka grpc con grpc-web
  val akkaGrpcRuntime = "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % akkaGrpcVersion
  val akkaManagement =
    "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion exclude ("com.typesafe.akka", "akka-http")

  val kamonBundle = "io.kamon" %% "kamon-bundle" % kamonVersion
  val kamonApmReporter = "io.kamon" %% "kamon-apm-reporter" % kamonVersion

  val akkaActorTypedTest = "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
  val akkaStreamTest = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
  val akkaPersistenceTest = "com.typesafe.akka" %% "akka-persistence-testkit" % akkaVersion % Test
}
