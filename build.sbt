organization := "lt.dvim.hof"
name := "hof"
description := "Tools for working with history files of fish shell"

scalaVersion := "2.13.1"

val Akka = "2.6.10"
val GraalAkka = "0.5.0"
val Monocle = "2.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor"        % Akka,
  "com.typesafe.akka"          %% "akka-stream"       % Akka exclude ("com.typesafe.akka", "akka-protobuf-v3_2.13"),
  "com.github.vmencik"         %% "graal-akka-actor"  % GraalAkka,
  "com.github.vmencik"         %% "graal-akka-stream" % GraalAkka,
  "com.github.julien-truffaut" %% "monocle-core"      % Monocle,
  "com.github.julien-truffaut" %% "monocle-macro"     % Monocle,
  "com.monovore"               %% "decline"           % "1.3.0",
  "org.typelevel"              %% "cats-effect"       % "2.2.0",
  "com.lihaoyi"                %% "fansi"             % "0.2.9",
  "org.scalameta"              %% "munit"             % "0.7.16" % Test
)

testFrameworks += new TestFramework("munit.Framework")

scalafmtOnCompile := true
scalafixOnCompile := true

ThisBuild / scalafixDependencies ++= Seq(
  "com.nequissimus" %% "sort-imports" % "0.5.4"
)

enablePlugins(AutomateHeaderPlugin)
startYear := Some(2020)
organizationName := "History of Fishing"
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

bintrayOrganization := Some("2m")
homepage := Some(url("https://github.com/2m/history-of-fishing"))
scmInfo := Some(
  ScmInfo(url("https://github.com/2m/history-of-fishing"), "git@github.com:2m/history-of-fishing.git")
)
developers += Developer(
  "contributors",
  "Contributors",
  "https://gitter.im/2m/general",
  url("https://github.com/2m/history-of-fishing/graphs/contributors")
)

buildInfoKeys := Seq[BuildInfoKey](version)
buildInfoPackage := "lt.dvim.hof"

enablePlugins(JavaAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)
enablePlugins(BuildInfoPlugin)
