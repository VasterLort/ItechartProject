package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class GeneralConf(
                        resourcePath: String,
                        contentDelimiterOfFile: String,
                        rowDelimiterOfFile: String,
                        startIndex: Int,
                        fileNameIndex: Int
                      )

object GeneralConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = GeneralConf(
    configLoader.getString("general.resourcePath"),
    configLoader.getString("general.contentDelimiterOfFile"),
    configLoader.getString("general.rowDelimiterOfFile"),
    configLoader.getInt("general.startIndex"),
    configLoader.getInt("general.fileNameIndex"))
}
