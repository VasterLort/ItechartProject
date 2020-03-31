package by.itechart.service

import by.itechart.action._
import by.itechart.conversion.XlsxToCsvConverter
import by.itechart.sftp.SftpConnection

class RetrievalService(val sftpConnection: SftpConnection.type = SftpConnection) {
  def getPaymentFile(fileName: String): Notice = {
    checkFormatPaymentFile(sftpConnection.getPaymentFile(fileName))
  }

  private def checkFormatPaymentFile(paymentFile: Notice): Notice = {
    paymentFile match {
      case fileName: CsvPaymentFile => fileName
      case fileName: XlsxPaymentFileName => XlsxToCsvConverter.convert(fileName.name)
      case _: InvalidFileName => InvalidFileName()
    }
  }
}
