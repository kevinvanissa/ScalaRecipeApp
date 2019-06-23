name := """scala-web-project"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  evolutions,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "mysql" % "mysql-connector-java" % "5.1.45",
  "org.scalikejdbc" %% "scalikejdbc" % "3.2.1",
  "de.svenkubiak" % "jBCrypt" % "0.4.1",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.2.1",
  "ch.qos.logback"  %  "logback-classic" % "1.2.3",
  "javax.xml.bind" % "jaxb-api" % "2.3.0"
 )

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
