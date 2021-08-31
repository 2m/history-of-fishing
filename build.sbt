organization := "lt.dvim.hof"
name := "hof"
description := "Tools for working with history files of fish shell"

scalaVersion := "2.13.6"

val Akka = "2.6.16"
val GraalAkka = "0.5.0"
val Monocle = "2.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"        % Akka,
  "com.typesafe.akka"  %% "akka-stream"       % Akka exclude ("com.typesafe.akka", "akka-protobuf-v3_2.13"),
  "com.github.vmencik" %% "graal-akka-actor"  % GraalAkka,
  "com.github.vmencik" %% "graal-akka-stream" % GraalAkka,
  "com.github.julien-truffaut" %% "monocle-core"  % Monocle,
  "com.github.julien-truffaut" %% "monocle-macro" % Monocle,
  "com.monovore"               %% "decline"       % "2.1.0",
  "org.typelevel"              %% "cats-effect"   % "3.2.5",
  "com.lihaoyi"                %% "fansi"         % "0.2.14",
  "org.scalameta"              %% "munit"         % "0.7.29" % Test
)

testFrameworks += new TestFramework("munit.Framework")

scalafmtOnCompile := true
scalafixOnCompile := true

ThisBuild / scalafixDependencies ++= Seq(
  "com.nequissimus" %% "sort-imports" % "0.5.5"
)

enablePlugins(AutomateHeaderPlugin)
startYear := Some(2020)
organizationName := "github.com/2m/yabai-scala/history-of-fishing"
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/2m/history-of-fishing"))
developers += Developer(
  "contributors",
  "Contributors",
  "https://gitter.im/2m/general",
  url("https://github.com/2m/history-of-fishing/contributors")
)
sonatypeProfileName := "lt.dvim"

buildInfoKeys := Seq[BuildInfoKey](version)
buildInfoPackage := "lt.dvim.hof"

enablePlugins(JavaAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)
enablePlugins(BuildInfoPlugin)
