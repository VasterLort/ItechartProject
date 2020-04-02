package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class GeneralConf(
                        resourcePath: String,
                        contentDelimiterOfFile: String,
                        rowDelimiterOfFile: String,
                        stringDelimiter: String,
                        falseStatement: String,
                        singlePaymentLength: Int,
                        startIndex: Int,
                        fileNameIndex: Int,
                        headIndex: Int
                      )

object GeneralConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = GeneralConf(
    configLoader.getString("general.resourcePath"),
    configLoader.getString("general.contentDelimiterOfFile"),
    configLoader.getString("general.rowDelimiterOfFile"),
    configLoader.getString("general.stringDelimiter"),
    configLoader.getString("general.falseStatement"),
    configLoader.getInt("general.singlePaymentLength"),
    configLoader.getInt("general.startIndex"),
    configLoader.getInt("general.fileNameIndex"),
    configLoader.getInt("general.headIndex"))
}
