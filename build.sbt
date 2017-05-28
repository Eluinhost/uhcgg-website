import sbt.Keys._
import sbt.Project.projectToRef

lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    name := "shared",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedBackend  = shared.jvm.settings(name := "sharedBackend")
lazy val sharedFrontend = shared.js.settings(name := "sharedFrontend")

lazy val frontend = (project in file("frontend"))
  .settings(
    name := "frontend",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    // setup scalajs + npm dependencies
    libraryDependencies ++= Settings.frontendDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // use Scala.js provided launcher code to start the client app
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework"),
    emitSourceMaps := true
  )
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
  .dependsOn(sharedFrontend)

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Seq(
      "Bartek's repo at Bintray" at "https://dl.bintray.com/btomala/maven",
      "jitpack" at "https://jitpack.io"
    ),
    libraryDependencies ++= Settings.backendDependencies.value,
//    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
    commands += ReleaseCmd,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    // connect to the client project
    scalaJSProjects := Seq(frontend),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    managedClasspath in Runtime += (packageBin in Assets).value,
    LessKeys.compress in Assets := true,
    WebKeys.packagePrefix in Assets := "public/",
    parallelExecution in Test := false // otherwise migrations explode
  )
  .enablePlugins(SbtTwirl, JavaAppPackaging, WebScalaJSBundlerPlugin)
//  .aggregate(frontends.map(projectToRef): _*)
  .dependsOn(sharedBackend)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") { state =>
  "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
    "frontend/clean" ::
    "frontend/test" ::
    "backend/clean" ::
    "backend/test" ::
    "backend/universal:packageBin" ::
    "set elideOptions in frontend := Seq()" ::
    state
}

// load backend by default
onLoad in Global := (Command.process("project backend", _: State)) compose (onLoad in Global).value

lazy val seed = (project in file("seed"))
  .settings(
    name := "seed",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.seedDependencies.value

    //    javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
  )
