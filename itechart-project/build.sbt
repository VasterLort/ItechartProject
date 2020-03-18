name := "itechart-project"
version := "0.1"
scalaVersion := "2.13.1"

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost/project"
flywayUser := "postgres"
flywayPassword := "kanekiken1998"
flywayLocations += "db/migration"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"
libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.18.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.11"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.flywaydb" % "flyway-core" % "6.3.1"

