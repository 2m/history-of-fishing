organization := "lt.dvim.hof"
name := "hof"
description := "Tools for working with history files of fish shell"

scalaVersion := "2.13.8"

val Akka = "2.6.18"
val Monocle = "2.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor"    % Akka,
  "com.typesafe.akka"          %% "akka-stream"   % Akka,
  "com.github.julien-truffaut" %% "monocle-core"  % Monocle,
  "com.github.julien-truffaut" %% "monocle-macro" % Monocle,
  "com.monovore"               %% "decline"       % "2.2.0",
  "org.typelevel"              %% "cats-effect"   % "3.3.4",
  "com.lihaoyi"                %% "fansi"         % "0.3.0",
  "org.scalameta"              %% "munit"         % "0.7.29" % Test
)

testFrameworks += new TestFramework("munit.Framework")

scalafmtOnCompile := true
scalafixOnCompile := true

ThisBuild / scalafixDependencies ++= Seq(
  "com.nequissimus" %% "sort-imports" % "0.6.1"
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

nativeImageOptions ++= List(
  "--verbose",
  "--no-fallback",
  "--initialize-at-build-time",
  "--allow-incomplete-classpath",
  "--report-unsupported-elements-at-runtime",
  "--initialize-at-run-time=scala.util.Random$",
  "--initialize-at-build-time=scala.runtime.Statics$VM"
)
nativeImageVersion := "21.2.0"
nativeImageAgentOutputDir := (Compile / resourceDirectory).value / "META-INF" / "native-image" / organization.value / name.value

enablePlugins(JavaAppPackaging)
enablePlugins(BuildInfoPlugin)
enablePlugins(NativeImagePlugin)
