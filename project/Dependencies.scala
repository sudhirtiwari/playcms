import sbt._

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