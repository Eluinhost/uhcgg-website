import sbt.Keys._

lazy val frontend = (project in file("frontend"))

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Settings.extraResolvers.value,
    libraryDependencies ++= Settings.backendDependencies.value,
//    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
    commands += ReleaseCmd,
    parallelExecution in Test := false // otherwise migrations explode
  )
  .enablePlugins(SbtTwirl, JavaAppPackaging)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") { state ⇒
  "backend/test" :: "backend/universal:packageBin" :: state
}

// load backend by default
onLoad in Global := (Command.process("project backend", _: State)) compose (onLoad in Global).value

lazy val seed = (project in file("seed"))
  .settings(
    name := "seed",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Settings.extraResolvers.value,
    libraryDependencies ++= Settings.seedDependencies.value
    //    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
  )
