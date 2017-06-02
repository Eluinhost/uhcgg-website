import sbt.Keys._

lazy val yarn = inputKey[Unit]("Run a yarn task")
lazy val feBuild = taskKey[Unit]("Runs the full frontend build")
lazy val install = taskKey[Unit]("Installs FE dependencies")
lazy val build = taskKey[Unit]("Builds FE code")
lazy val graphql = taskKey[Unit]("Builds the schema.tsx file from the existing schema")
lazy val schema = taskKey[Unit]("Generates a new schema.json from the endpoint")

val shell = if (sys.props("os.name").contains("Windows")) "cmd" :: "/c" :: "yarn" :: Nil else "bash" :: "-c" :: "yarn" :: Nil

lazy val frontend = (project in file("frontend"))
  .settings(
    name := Settings.name + ".frontend",
    organization := Settings.organisation,
    version := Settings.version,
    crossPaths := false,
    autoScalaLibrary := false,
    yarn := {
      val args = complete.DefaultParsers.spaceDelimited("<arg>").parsed

      val result = Process(shell ++ args, baseDirectory.value) ! streams.value.log

      if (result != 0)
        sys.error("task failed")
    },
    install := yarn.toTask(" install").value,
    graphql := yarn.toTask(" graphql").value,
    build := yarn.toTask(" build").value,
    schema := yarn.toTask(" schema").value,
    // Make compile run the yarn build first
    compile := ((compile in Compile) dependsOn feBuild).value,
    // Add the build directory to the list of resources
    unmanagedResourceDirectories in Compile += baseDirectory.value / "dist",
    // Full build should clean + generate schema.tsx + build
    feBuild := (build dependsOn graphql dependsOn install dependsOn clean).value,
    // Add the build directory to the list of files to clean up
    cleanFiles += baseDirectory { base ⇒ base / "dist" }.value
  )

lazy val backend = (project in file("backend"))
  .settings(
    name := Settings.name + ".backend",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Settings.extraResolvers.value,
    libraryDependencies ++= Settings.backendDependencies.value,
//    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
    commands += ReleaseCmd,
    parallelExecution in Test := false // otherwise migrations explode
  )
  .enablePlugins(JavaAppPackaging)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") { state ⇒
  "backend/test" :: "backend/universal:packageBin" :: state
}

lazy val seed = (project in file("seed"))
  .settings(
    name := Settings.name + ".seed",
    organization := Settings.organisation,
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Settings.extraResolvers.value,
    libraryDependencies ++= Settings.seedDependencies.value
    //    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
  )

// load backend by default
onLoad in Global := (Command.process("project backend", _: State)) compose (onLoad in Global).value