name := "itechart-project"
version := "0.1"
scalaVersion := "2.13.1"

libraryDependencies += "org.flywaydb" % "flyway-sbt" % "4.2.0"

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost/project"
flywayUser := "postgres"
flywayPassword := "kanekiken1998"
flywayLocations += "db/migration"