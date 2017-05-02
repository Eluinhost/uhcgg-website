import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Settings {
  val name    = "uhcgg-mvp"
  val version = "0.0.1"

  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  object versions {
    val scala        = "2.12.2"
    val scalaDom     = "0.9.1"
    val scalajsReact = "1.0.0"
    val scalaCSS     = "0.5.3"
    val autowire     = "0.2.6"
    val booPickle    = "1.2.5"
    val diode        = "1.1.1"
    val uTest        = "0.4.4"
    val react        = "15.5.4"
  }

  val sharedDependencies = Def.setting(
    Seq(
      "com.lihaoyi" %%% "autowire"  % versions.autowire,
      "me.chrons"   %%% "boopickle" % versions.booPickle
    )
  )

  val backendDependencies = Def.setting(
    Seq(
      "org.webjars"                        % "font-awesome"     % "4.3.0-1" % Provided,
      "com.lihaoyi"                        %% "utest"           % versions.uTest % Test,
      "com.typesafe.akka"                  %% "akka-http"       % "10.0.4",
      "com.typesafe.akka"                  %% "akka-slf4j"      % "2.4.17",
      "ch.megard"                          %% "akka-http-cors"  % "0.1.11",
      "com.softwaremill.akka-http-session" %% "core"            % "0.4.0",
      "de.heikoseeberger"                  %% "akka-http-circe" % "1.15.0",
      "btomala"                            %% "akka-http-twirl" % "1.2.0",
      "org.tpolecat"                       %% "doobie-core"     % "0.4.1",
      "org.tpolecat"                       %% "doobie-hikari"   % "0.4.1",
      "org.tpolecat"                       %% "doobie-postgres" % "0.4.1",
      "com.github.t3hnar"                  %% "scala-bcrypt"    % "3.0",
      "org.postgresql"                     % "postgresql"       % "9.4-1201-jdbc41",
      "org.flywaydb"                       % "flyway-core"      % "3.2.1",
      "com.zaxxer"                         % "HikariCP"         % "2.4.5",
      "ch.qos.logback"                     % "logback-classic"  % "1.1.3",
      "io.circe"                           %% "circe-generic"   % "0.7.1",
      "com.softwaremill.macwire"           %% "macros"          % "2.3.0" % Provided,
      "com.softwaremill.macwire"           %% "util"            % "2.3.0",
      "org.sangria-graphql"                %% "sangria"         % "1.1.0",
      "org.sangria-graphql"                %% "sangria-circe"   % "1.0.1"
    )
  )

  val frontendDependencies = Def.setting(
    Seq(
      "com.github.japgolly.scalajs-react" %%% "core"      % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra"     % versions.scalajsReact,
      "com.github.japgolly.scalacss"      %%% "ext-react" % versions.scalaCSS,
//      "io.suzaku"                         %%% "diode"       % versions.diode,
//      "io.suzaku"                         %%% "diode-react" % versions.diode,
      "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
      "com.lihaoyi"  %%% "utest"       % versions.uTest % Test
    )
  )

  val jsDependencies = Def.setting(
    Seq(
      "react"     → versions.react,
      "react-dom" → versions.react
    )
  )
}
