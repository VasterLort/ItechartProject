package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class DatabaseConf(
                         dbUrl: String,
                         dbUsername: String,
                         dbPassword: String
                       )

object DatabaseConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = DatabaseConf(
    configLoader.getString("database.url"),
    configLoader.getString("database.user"),
    configLoader.getString("database.password"))
}
