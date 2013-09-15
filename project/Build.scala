import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  import Dependencies._

  val appName         = "playcms"
  val appVersion      = "0.1.0-SNAPSHOT"

  resolvers ++= resolutionRepos

  val appDependencies = compileDeps ++ testDeps

  lazy val UnitTest = config("unit") extend Test

  lazy val main = play.Project(
    appName,
    appVersion,
    appDependencies
  )
  .configs(UnitTest)
  .settings(
    organization := "com.github.nrf110",
    resolvers ++= resolutionRepos,
    testOptions in Test := Seq(
      Tests.Setup { () => System.setProperty("config.file", "conf/test.conf") }
    ),
    testOptions in UnitTest := Seq(
      Tests.Setup { () => System.setProperty("config.file", "conf/test.conf") },
      Tests.Filter { _.contains(".unit.") }
    ),
    parallelExecution in Test := false,
    parallelExecution in UnitTest := false,
    sbt.Keys.fork in Test := false
  )

  lazy val testModule = play.Project(
    appName + "-test",
    appVersion,
    path = file("./test-host")
  ).dependsOn(main)
}

object Dependencies {
  val resolutionRepos = Seq(
  )

  object V {
    val reactive  = "0.9"
    val scalatest = "2.0.M5b"
    val handlebars = "0.9.0"
  }

  val compileDeps = Seq(
    "org.reactivemongo"       %% "play2-reactivemongo"           % V.reactive,
    "com.github.jknack"       %  "handlebars"                    % V.handlebars
  )
  val testDeps = Seq(
    "org.scalatest"           %% "scalatest"         % V.scalatest
  )
}
