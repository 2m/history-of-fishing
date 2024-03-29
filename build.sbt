organization := "lt.dvim.hof"
name := "hof"
description := "Tools for working with history files of fish shell"

scalaVersion := "2.13.10"

val Akka = "2.6.20"
val Monocle = "3.2.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"    % Akka,
  "com.typesafe.akka" %% "akka-stream"   % Akka,
  "dev.optics"        %% "monocle-core"  % Monocle,
  "dev.optics"        %% "monocle-macro" % Monocle,
  "com.monovore"      %% "decline"       % "2.4.1",
  "org.typelevel"     %% "cats-effect"   % "3.5.0",
  "com.lihaoyi"       %% "fansi"         % "0.4.0",
  "org.scalameta"     %% "munit"         % "0.7.29" % Test
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
nativeImageJvm := "graalvm-java17"
nativeImageVersion := "22.3.1"
nativeImageAgentOutputDir := (Compile / resourceDirectory).value / "META-INF" / "native-image" / organization.value / name.value

enablePlugins(JavaAppPackaging)
enablePlugins(BuildInfoPlugin)
enablePlugins(NativeImagePlugin)
