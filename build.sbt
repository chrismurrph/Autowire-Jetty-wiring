import sbt.Keys._

name := "Wiring Example"

lazy val root = project.in(file(".")).aggregate(wiringJS, wiringJVM).settings(
    publish := {},
    publishLocal := {},
    scalaVersion := "2.11.6"
  )
val wiring = crossProject.in(file(".")).settings(
  version := "0.1-SNAPSHOT",
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value / "shared" / "src" / "main" / "scala",  
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.3.4",
    "com.lihaoyi" %%% "autowire" % "0.2.5",
    "com.lihaoyi" %%% "scalatags" % "0.5.2"
  ),
  scalaVersion := "2.11.6"  
).jsSettings(
  name := "Client",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public")
  ),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "fastparse" % "0.2.1",
    "org.mdedetrich" %%% "soda-time" % "0.0.1-SNAPSHOT"
  )
).jvmSettings(
  name := "Server",
  libraryDependencies ++= Seq(
    "io.spray" %% "spray-routing-shapeless2" % "1.3.3",
    "io.spray" %% "spray-servlet" % "1.3.3",
    "com.typesafe.akka" %% "akka-actor" % "2.3.9",
    "org.webjars" % "bootstrap" % "3.2.0",
    "org.me" %% "db-connect" % "0.3"
  )
)
lazy val wiringJS = wiring.js
lazy val wiringJVM = wiring.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in (wiringJS, Compile)).value
    (artifactPath in (wiringJS, Compile, fastOptJS)).value
  }
)
