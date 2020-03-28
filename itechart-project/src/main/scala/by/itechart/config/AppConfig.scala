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
                      resourcePath: String,
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
    configLoader.getString("general.resourcePath"),
    configLoader.getInt("http.port"))
}
