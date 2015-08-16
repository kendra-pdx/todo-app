import sbt._, Keys._

object Boilerplate extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.7",
    organization := "enkode.me",
    addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.12"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint", "-Xfatal-warnings", "-Yinline-warnings"))

  object Modules {
    val akkaStreamsVersion = "1.0"
    def akka(name: String, version: String = "2.3.12") = "com.typesafe.akka" %% s"akka-$name" % version

    def slf4j(name: String) = "org.slf4j" % s"slf4j-$name" % "1.7.10"

    lazy val slf4j_api = slf4j("api")
    lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"

    lazy val scala_xml = "org.scala-lang.modules" %% "scala-xml" % "1.0.4"

    lazy val μPickle = "com.lihaoyi" %% "upickle" % "0.3.4"

    lazy val logging = slf4j_api :: logback :: Nil
  }
}