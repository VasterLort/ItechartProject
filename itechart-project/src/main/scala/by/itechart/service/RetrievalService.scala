package by.itechart.service

import by.itechart.action._
import by.itechart.config.AppConfig
import by.itechart.conversion.Converter
import by.itechart.sftp.SftpConnection

class RetrievalService {
  def getPaymentFile(): Notice = {
    checkFormatPaymentFile(SftpConnection.connectSftpServer())
  }

  private def checkFormatPaymentFile(paymentFile: Notice): Notice = {
    paymentFile match {
      case fileName: CsvPaymentFile => fileName
      case fileName: XlsxPaymentFileName => Converter.convertXlsxToCsv(fileName.name)
      case _: InvalidFileName => InvalidFileName()
      case _: EmptyFolder => EmptyFolder()
    }
  }
}
