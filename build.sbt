import Dependencies._

name := "pocs - ms"

lazy val root = (project in file("."))
  .aggregate(back)

lazy val back = (project in file("back"))
  .enablePlugins()
  .settings(
    name := "poc-ms-akka",
    scalaVersion := "2.13.6" /*"3.0.2"*/,
    ThisBuild / dynverSeparator := "-",
    topLevelDirectory := None,
    libraryDependencies ++= Seq(scribe, scopt, scalaTest),
  )

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / scalacOptions ++= Seq(
  "-Xsource:3"
  //"-P:silencer:pathFilters=.*[/]src_managed[/].*"
  //"-Wconf:src=src_managed/.*:silent"
)
