package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class DictionaryConf(
                           company: String,
                           department: String,
                           payDate: String
                         )

object DictionaryConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = DictionaryConf(
    configLoader.getString("dictionary.company"),
    configLoader.getString("dictionary.department"),
    configLoader.getString("dictionary.payDate"))
}
