name := "uhcgg-mvp"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += "Bartek's repo at Bintray" at "https://dl.bintray.com/btomala/maven"

enablePlugins(SbtTwirl)

libraryDependencies ++= Seq(
  "com.typesafe.akka"                  %% "akka-http"            % "10.0.4",
  "com.typesafe.akka"                  %% "akka-slf4j"           % "2.4.17",
  "ch.megard"                          %% "akka-http-cors"       % "0.1.11",
  "com.softwaremill.akka-http-session" %% "core"                 % "0.4.0",
  "com.typesafe.akka"                  %% "akka-http-spray-json" % "10.0.4",
  "btomala"                            %% "akka-http-twirl"      % "1.2.0",
  "org.tpolecat"                       %% "doobie-core"          % "0.4.1",
  "org.tpolecat"                       %% "doobie-hikari"        % "0.4.1",
  "org.tpolecat"                       %% "doobie-postgres"      % "0.4.1",
  "com.github.t3hnar"                  %% "scala-bcrypt"         % "3.0",
  "org.postgresql"                     % "postgresql"            % "9.4-1201-jdbc41",
  "org.flywaydb"                       % "flyway-core"           % "3.2.1",
  "com.zaxxer"                         % "HikariCP"              % "2.4.5",
  "ch.qos.logback"                     % "logback-classic"       % "1.1.3"
)

watchSources := (watchSources.value --- baseDirectory.value / "src" / "main" / "resources" / "build" ** "*").get