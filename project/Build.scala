import sbt._
import Keys._
import BuildSettings._

object ApplicationBuild extends Build {
  val appName         = "playcms"
  val appVersion      = "0.1.0-SNAPSHOT"

  lazy val util = sbt.Project(
    id = appName + "-util",
    base = file("util"),
    settings = Defaults.defaultSettings ++ commonSettings
  )

  lazy val common = play.Project(
    name = appName + "-common",
    applicationVersion = appVersion,
    dependencies = appDependencies,
    path = file("common"),
    settings = commonSettings
  ).dependsOn(util)

  lazy val admin = play.Project(
    name = appName + "-admin",
    applicationVersion = appVersion,
    dependencies = appDependencies,
    path = file("admin"),
    settings = commonSettings ++ Seq(
      // Turn off play's internal less compiler
      play.Project.lessEntryPoints := Nil,
      // Turn off play's internal javascript compiler
      play.Project.javascriptEntryPoints := Nil,
      // tell play we're using this directory for throwing Grunt-built assets in, so it will allow fetching updated ones from the File System
      play.Project.playAssetsDirectories <+= (baseDirectory in Compile)(base => base / "public")
    )
  ).dependsOn(util, common)

  lazy val renderer = play.Project(
    name = appName + "-renderer",
    applicationVersion = appVersion,
    dependencies = appDependencies,
    path = file("renderer"),
    settings = commonSettings
  ).dependsOn(util, common)

  lazy val main = play.Project(
    appName + "-test",
    appVersion,
    path = file("./test-host"),
    settings = commonSettings
  ).dependsOn(util, admin, renderer).aggregate(util, admin, renderer)
}