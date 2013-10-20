import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  import Dependencies._

  val appName         = "playcms"
  val appVersion      = "0.1.0-SNAPSHOT"

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
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
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
  )
  .settings(
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  ).dependsOn(main)
}

object Dependencies {
  object Group {
    val akka = "com.typesafe.akka"
  }

  object V {
    val reactive  = "0.10.0-SNAPSHOT"
    val scalatest = "2.0.M5b"
    val handlebars = "0.9.0"
    val akka = "2.2.1"
    val play = "2.2.0"
  }

  val compileDeps = Seq(
    "org.reactivemongo"       %% "play2-reactivemongo"           % V.reactive,
    "com.github.jknack"       %  "handlebars"                    % V.handlebars,
    Group.akka                %% "akka-actor"                    % V.akka,
    Group.akka                %% "akka-camel"                    % V.akka,
    "com.typesafe.play"       %% "play-cache"                    % V.play
  )
  val testDeps = Seq(
    "org.scalatest"           %% "scalatest"         % V.scalatest
  )
}
