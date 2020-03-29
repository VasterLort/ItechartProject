package by.itechart.config

import com.typesafe.config.ConfigFactory

case class AppConfig(
                      dbUrl: String,
                      dbUsername: String,
                      dbPassword: String,
                      sftpHost: String,
                      sftpPort: Int,
                      sftpUsername: String,
                      sftpPassword: String,
                      sftpPath: String,
                      sftpType: String,
                      resourcePath: String,
                      contentDelimiterOfFile: String,
                      rowDelimiterOfFile: String,
                      startIndex: Int,
                      fileNameIndex: Int,
                      swaggerHost: String,
                      swaggerPathPrefix: String,
                      swaggerResource: String,
                      swaggerResourceDirectory: String,
                      httpPort: Int
                    )

object AppConfig {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = AppConfig(
    configLoader.getString("database.url"),
    configLoader.getString("database.user"),
    configLoader.getString("database.password"),
    configLoader.getString("sftp.host"),
    configLoader.getInt("sftp.port"),
    configLoader.getString("sftp.username"),
    configLoader.getString("sftp.password"),
    configLoader.getString("sftp.path"),
    configLoader.getString("sftp.type"),
    configLoader.getString("general.resourcePath"),
    configLoader.getString("general.contentDelimiterOfFile"),
    configLoader.getString("general.rowDelimiterOfFile"),
    configLoader.getInt("general.startIndex"),
    configLoader.getInt("general.fileNameIndex"),
    configLoader.getString("swagger.host"),
    configLoader.getString("swagger.pathPrefix"),
    configLoader.getString("swagger.resource"),
    configLoader.getString("swagger.resourceDirectory"),
    configLoader.getInt("http.port"))
}
