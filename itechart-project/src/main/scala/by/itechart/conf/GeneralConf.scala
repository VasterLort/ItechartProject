package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class GeneralConf(
                        resourcePath: String
                      )

object GeneralConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = GeneralConf(configLoader.getString("general.resourcePath"))
}
