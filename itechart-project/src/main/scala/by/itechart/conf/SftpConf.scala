package by.itechart.conf

import com.typesafe.config.ConfigFactory

case class SftpConf(
                     sftpHost: String,
                     sftpPort: Int,
                     sftpUsername: String,
                     sftpPassword: String,
                     sftpPath: String,
                     sftpType: String
                   )

object SftpConf {
  private lazy val configLoader = ConfigFactory.load("application.conf")
  lazy val configValues = SftpConf(
    configLoader.getString("sftp.host"),
    configLoader.getInt("sftp.port"),
    configLoader.getString("sftp.username"),
    configLoader.getString("sftp.password"),
    configLoader.getString("sftp.path"),
    configLoader.getString("sftp.type"))
}
