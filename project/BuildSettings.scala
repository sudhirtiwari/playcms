import sbt._
import Keys._

object BuildSettings {
  import Dependencies._

  val appName = "playcms"
  val appVersion = "0.1.0-SNAPSHOT"
  val appDependencies = compileDeps ++ testDeps

  val commonSettings = Seq(
    organization := "com.github.nrf110",
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    parallelExecution in Test := false,
    sbt.Keys.fork in Test := false
  )
}