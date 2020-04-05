package by.itechart.sftp

import by.itechart.action._
import by.itechart.conf.{GeneralConf, SftpConf}
import by.itechart.constant.Constant
import com.jcraft.jsch.{ChannelSftp, JSch}

import scala.io.Source

object SftpConnection {
  def getPaymentFile(fileName: String): Notice = {
    connectSftpServer(PaymentFileAction(fileName))
  }

  def getPaymentFileName: Notice = {
    connectSftpServer(PaymentFileNameAction())
  }

  private def connectSftpServer(action: Notice): Notice = {
    val jsch = new JSch()
    val session = jsch.
      getSession(SftpConf.configValues.sftpUsername, SftpConf.configValues.sftpHost, SftpConf.configValues.sftpPort)
    session.setConfig("StrictHostKeyChecking", "no")
    session.setPassword(SftpConf.configValues.sftpPassword)
    session.connect()

    val result = selectAction(action, session.openChannel(SftpConf.configValues.sftpType).asInstanceOf[ChannelSftp])

    session.disconnect()

    result
  }

  private def selectAction(action: Notice, channel: ChannelSftp): Notice = {
    action match {
      case action: PaymentFileAction => readPaymentFile(channel, action.name)
      case _: PaymentFileNameAction => readPaymentFileName(channel)
    }
  }

  private def readPaymentFile(channel: ChannelSftp, fileName: String): Notice = {
    channel.connect()
    val result = checkPaymentFileName(fileName) match {
      case fileCsv: CsvPaymentFileName =>
        val content = Source.fromInputStream(channel.get(SftpConf.configValues.sftpPath + fileCsv.name)).mkString
        channel.rm(SftpConf.configValues.sftpPath + fileCsv.name)
        CsvPaymentFile(content, fileCsv.name)
      case fileCsv: XlsxPaymentFileName => {
        channel.get(SftpConf.configValues.sftpPath + fileCsv.name, GeneralConf.configValues.resourcePath)
        channel.rm(SftpConf.configValues.sftpPath + fileCsv.name)
        fileCsv
      }
      case _: InvalidFileName => InvalidFileName()
    }

    channel.disconnect()

    result
  }

  private def readPaymentFileName(channel: ChannelSftp): Notice = {
    channel.connect()
    val result = channel.ls(SftpConf.configValues.sftpPath) match {
      case folder if folder.isEmpty => EmptyFolder()
      case fileNames => PaymentFileName(fileNames)
    }

    channel.disconnect()

    result
  }

  private def checkPaymentFileName(paymentFileName: String): Notice = {
    paymentFileName.substring(paymentFileName.lastIndexOf(Constant.UselessInfo) + Constant.FileNameIndex) match {
      case fileName if fileName.endsWith(Constant.CsvFormat) => CsvPaymentFileName(fileName)
      case fileName if fileName.endsWith(Constant.XlsxFormat) => XlsxPaymentFileName(fileName)
      case _ => InvalidFileName()
    }
  }
}
