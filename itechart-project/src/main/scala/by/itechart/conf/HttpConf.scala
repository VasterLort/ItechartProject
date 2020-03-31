package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class HttpConf(
                     httpPort: Int,
                     httpInterface: String
                   )

object HttpConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = HttpConf(
    configLoader.getInt("http.port"),
    configLoader.getString("http.interface"))
}
