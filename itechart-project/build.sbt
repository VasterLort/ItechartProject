name := "itechart-project"
version := "0.1"
scalaVersion := "2.13.1"

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost/project"
flywayUser := "postgres"
flywayPassword := "kanekiken1998"
flywayLocations += "db/migration"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-typed" % "2.6.3"
libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
libraryDependencies += "org.iq80.leveldb" % "leveldb" % "0.7"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"
libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.18.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.11"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.flywaydb" % "flyway-core" % "6.3.1"

