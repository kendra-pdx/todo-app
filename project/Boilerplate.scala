import sbt._, Keys._

object Boilerplate extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.7",
    organization := "enkode.me",
    addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.12"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint", "-Xfatal-warnings", "-Yinline-warnings"))

  object Modules {
    object Versions {
      val µPickle = "0.3.6"
      val scalaJsReact = "0.10.2"
      val scalaJsDom = "0.8.2"

      val react = "0.14.3"

      val akkaStreams = "2.0-M1"
      val akka = "2.4.1"

      val slf4j = "1.7.10"
      val logback = "1.1.2"
      val scalaXml = "1.0.4"
    }

    def akka(name: String, version: String = Versions.akka) = "com.typesafe.akka" %% s"akka-$name" % version

    def slf4j(name: String) = "org.slf4j" % s"slf4j-$name" % Versions.slf4j

    lazy val slf4j_api = slf4j("api")
    lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    lazy val scala_xml = "org.scala-lang.modules" %% "scala-xml" % Versions.scalaXml

    lazy val μPickle = "com.lihaoyi" %% "upickle" % Versions.µPickle

    lazy val logging = slf4j_api :: logback :: Nil
  }
}