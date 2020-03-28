package by.itechart.sftp

import by.itechart.action._
import by.itechart.config.AppConfig
import com.jcraft.jsch.{ChannelSftp, JSch}

import scala.io.Source

object SftpConnection {
  def connectSftpServer(): Notice = {
    val jsch = new JSch()
    val session = jsch.
      getSession(AppConfig.configValues.sftpUsername, AppConfig.configValues.sftpHost, AppConfig.configValues.sftpPort)
    session.setConfig("StrictHostKeyChecking", "no")
    session.setPassword(AppConfig.configValues.sftpPassword)
    session.connect()

    val result = getPaymentFile(session.openChannel("sftp").asInstanceOf[ChannelSftp])

    session.disconnect()

    result
  }

  private def getPaymentFile(channel: ChannelSftp): Notice = {
    channel.connect()
    val paymentFileName = channel.ls(AppConfig.configValues.sftpPath) match {
      case folder if folder.isEmpty => EmptyFolder()
      case _ => checkPaymentFileName(channel.ls(AppConfig.configValues.sftpPath).get(0).toString) match {
        case fileName: CsvPaymentFileName =>
          val content = Source.fromInputStream(channel.get(AppConfig.configValues.sftpPath + fileName.name)).mkString
          channel.rm(AppConfig.configValues.sftpPath + fileName.name)
          CsvPaymentFile(content, fileName.name)
        case fileName: XlsxPaymentFileName => {
          channel.get(AppConfig.configValues.sftpPath + fileName.name, AppConfig.configValues.resourcePath)
          channel.rm(AppConfig.configValues.sftpPath + fileName.name)
          fileName
        }
        case _: InvalidFileName => InvalidFileName()
      }
    }

    channel.disconnect()

    paymentFileName
  }

  private def checkPaymentFileName(paymentFileName: String): Notice = {
    paymentFileName.substring(paymentFileName.lastIndexOf(' ') + 1) match {
      case fileName if fileName.matches("\\w+_\\w+_\\d{8}\\.csv") => CsvPaymentFileName(fileName)
      case fileName if fileName.matches("\\w+_\\w+_\\d{8}\\.xlsx") => XlsxPaymentFileName(fileName)
      case _ => InvalidFileName()
    }
  }
}
