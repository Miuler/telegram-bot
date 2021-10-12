import Dependencies._
import com.github.ingarabr.DeployConfiguration

name := "telegram-bot"

lazy val root = (project in file("."))
  .aggregate(back)

lazy val back = (project in file("back"))
  .enablePlugins(CloudFunctionsPlugin, NativeImagePlugin)
  .settings(
    name := "telegram-bot",
    scalaVersion := "2.13.6" /*"3.0.2"*/,
    ThisBuild / dynverSeparator := "-",
    topLevelDirectory := None,
    libraryDependencies ++= Seq(scribe, scopt, gcFunctions, telegramBot, scalaTest, pureconfig, logbackClassic),
    nativeImageVersion := "21.2.0",
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    // =======================================================
    cloudFunctionClass := "infrastructure.TelegramBotFunctions",
    // The jar to deploy. Example using sbt-assembly
    cloudFunctionJar := assembly.value,
    // Environment deployment configuration.
    cloudFunctionDeployConfiguration := DeployConfiguration(
      functionName = "telegram-bot",
      gcpProject = "telegram-bot",
      gcpLocation = "us-central1",
      memoryMb = 512, // default value
      triggerHttp = true, // default value
      allowUnauthenticated = false, // default value
      runtime = "java11" // default value
    ),
    // The port used when testing the function locally.
    cloudFunctionPort := 8080 // default value
    // =======================================================
  )

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / scalacOptions ++= Seq(
  "-Xsource:3"
  //"-P:silencer:pathFilters=.*[/]src_managed[/].*"
  //"-Wconf:src=src_managed/.*:silent"
)
