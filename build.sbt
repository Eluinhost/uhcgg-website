
name := "uhcgg-mvp"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1",
  "ch.megard" %% "akka-http-cors" % "0.1.11",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.flywaydb" % "flyway-core" % "3.2.1",
  "com.zaxxer" % "HikariCP" % "2.4.5",
  "com.softwaremill.akka-http-session" %% "core" % "0.4.0"
)