name := "itechart-project"
version := "0.1"
scalaVersion := "2.13.1"

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost/project"
flywayUser := "postgres"
flywayPassword := "kanekiken1998"
flywayLocations += "db/migration"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.4"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.4"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.4"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"

libraryDependencies ++= Seq(
  "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.4",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.0.6",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.3",
  "io.swagger.core.v3" % "swagger-core" % "2.1.1",
  "io.swagger.core.v3" % "swagger-annotations" % "2.1.1",
  "io.swagger.core.v3" % "swagger-models" % "2.1.1",
  "io.swagger.core.v3" % "swagger-jaxrs2" % "2.1.1",
  "ch.megard" %% "akka-http-cors" % "0.4.2",
  "co.pragmati" %% "swagger-ui-akka-http" % "1.3.0"
)

libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
libraryDependencies += "org.iq80.leveldb" % "leveldb" % "0.7"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"
libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.18.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.11"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "org.flywaydb" % "flyway-core" % "6.3.1"

libraryDependencies += "io.jvm.uuid" %% "scala-uuid" % "0.3.1"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"

libraryDependencies += "commons-io" % "commons-io" % "2.6"

libraryDependencies += "org.apache.poi" % "poi" % "4.1.2"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "4.1.2"