import sbt._

name := "slick-android"

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.10.0")

libraryDependencies ++= Seq(
  "com.google.android" % "android" % "4.1.1.4",
  "org.slf4j" % "slf4j-android" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2" % "test"
)

parallelExecution in Test := false

