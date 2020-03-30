package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class SwaggerConf(
                        swaggerHost: String,
                        swaggerPathPrefix: String,
                        swaggerResource: String,
                        swaggerResourceDirectory: String
                      )

object SwaggerConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = SwaggerConf(
    configLoader.getString("swagger.host"),
    configLoader.getString("swagger.pathPrefix"),
    configLoader.getString("swagger.resource"),
    configLoader.getString("swagger.resourceDirectory"))
}
