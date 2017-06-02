import sbt._

object Settings {
  val organisation = "gg.uhc"
  val name         = "uhcgg-mvp"
  val version      = "0.0.1"

  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  val extraResolvers = Def.setting(
    Seq(
      "Bartek's repo at Bintray" at "https://dl.bintray.com/btomala/maven",
      "jitpack" at "https://jitpack.io"
    )
  )

  object versions {
    val scala           = "2.12.2"
    val diode           = "1.1.2"
    val bcrypt          = "3.0"
    val doobie          = "0.4.1"
    val circe           = "0.8.0"
    val akkaHttp        = "10.0.4"
    val akkaSlf4j       = "2.4.17"
    val akkaHttpSession = "0.4.0"
    val akkaHttpCors    = "0.1.11"
    val akkaHttpCirce   = "1.15.0"
    val postgresql      = "42.1.0"
    val flyway          = "3.2.1"
    val hikaricp        = "2.4.5"
    val logback         = "1.1.3"
    val macwire         = "2.3.0"
    val sangria         = "1.2.1"
    val sangriaCirce    = "1.0.1"
    val jwtCirce        = "0.12.1"
    val scalatest       = "3.0.1"
    val benerator       = "0.9.8"
    val databeneCommons = "1.0.0"
    val javafaker       = "0.13"
    val slf4jLog4j12    = "1.6.4"
  }

  val backendDependencies = Def.setting(
    Seq(
      "com.typesafe.akka"                  %% "akka-http"        % versions.akkaHttp,
      "com.typesafe.akka"                  %% "akka-slf4j"       % versions.akkaSlf4j,
      "ch.megard"                          %% "akka-http-cors"   % versions.akkaHttpCors,
      "com.softwaremill.akka-http-session" %% "core"             % versions.akkaHttpSession,
      "de.heikoseeberger"                  %% "akka-http-circe"  % versions.akkaHttpCirce,
      "org.tpolecat"                       %% "doobie-core"      % versions.doobie,
      "org.tpolecat"                       %% "doobie-hikari"    % versions.doobie,
      "org.tpolecat"                       %% "doobie-postgres"  % versions.doobie,
      "org.tpolecat"                       %% "doobie-scalatest" % versions.doobie,
      "com.github.t3hnar"                  %% "scala-bcrypt"     % versions.bcrypt,
      "org.postgresql"                     % "postgresql"        % versions.postgresql,
      "org.flywaydb"                       % "flyway-core"       % versions.flyway,
      "com.zaxxer"                         % "HikariCP"          % versions.hikaricp,
      "ch.qos.logback"                     % "logback-classic"   % versions.logback,
      "io.circe"                           %% "circe-generic"    % versions.circe,
      "io.circe"                           %% "circe-java8"      % versions.circe,
      "com.softwaremill.macwire"           %% "macros"           % versions.macwire % Provided,
      "com.softwaremill.macwire"           %% "util"             % versions.macwire,
      "org.sangria-graphql"                %% "sangria"          % versions.sangria,
      "org.sangria-graphql"                %% "sangria-circe"    % versions.sangriaCirce,
      "com.pauldijou"                      %% "jwt-circe"        % versions.jwtCirce,
      "org.scalatest"                      %% "scalatest"        % versions.scalatest % "test"
    )
  )

  val seedDependencies = Def.setting(
    Seq(
      "org.databene"         % "databene-benerator" % versions.benerator,
      "com.github.t3hnar"    %% "scala-bcrypt"      % versions.bcrypt,
      "org.databene"         % "databene-commons"   % versions.databeneCommons,
      "com.github.javafaker" % "javafaker"          % versions.javafaker,
      "org.slf4j"            % "slf4j-log4j12"      % versions.slf4jLog4j12
    )
  )
}
