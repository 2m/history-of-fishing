organization := "lt.dvim.hof"
name := "hof"
description := "Tools for working with history files of fish shell"

scalaVersion := "2.13.1"

val Akka = "2.6.3"
val Monocle = "2.0.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-stream"   % Akka,
  "com.github.julien-truffaut" %% "monocle-core"  % Monocle,
  "com.github.julien-truffaut" %% "monocle-macro" % Monocle,
  "com.monovore"               %% "decline"       % "1.0.0",
  "org.typelevel"              %% "cats-effect"   % "2.1.1",
  "com.lihaoyi"                %% "fansi"         % "0.2.8"
)

scalafmtOnCompile := true

ThisBuild / scalafixDependencies ++= Seq(
  "com.nequissimus" %% "sort-imports" % "0.3.1"
)

enablePlugins(AutomateHeaderPlugin)
startYear := Some(2020)
organizationName := "History of Fishing"
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
