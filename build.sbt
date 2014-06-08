import sbt._

name := "slickdroid"

organization := "com.geteit"

version := "0.1"

scalaVersion := "2.11.0"

crossScalaVersions ++= Seq("2.10.0")

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "reactive-couch releases" at "https://raw.github.com/zbsz/mvn-repo/master/releases/"

resolvers += "reactive-couch snapshots" at "https://raw.github.com/zbsz/mvn-repo/master/snapshots/"

libraryDependencies ++= Seq(
  "org.robolectric" % "android-all" % "4.3_r2-robolectric-0" % "provided",
  "org.slf4j" % "slf4j-android" % "1.7.7",
  "junit" % "junit" % "4.8.2" % "test",
  "com.geteit" %% "robotest" % "0.4" % "test",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2" % "test"
)

fork in Test := true

javaOptions in Test ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
