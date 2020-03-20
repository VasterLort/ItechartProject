package by.itechart.config

import com.typesafe.config.ConfigFactory

case class AppConfig(
                      dbUrl: String,
                      dbUsername: String,
                      dbPassword: String,
                      port: Int
                    )

object AppConfig {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = AppConfig(
    configLoader.getString("database.url"),
    configLoader.getString("database.user"),
    configLoader.getString("database.password"),
    configLoader.getInt("connection.port"))
}
