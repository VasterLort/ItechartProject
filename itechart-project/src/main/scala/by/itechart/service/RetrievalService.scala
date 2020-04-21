package by.itechart.service

import by.itechart.action._
import by.itechart.conversion.XlsxToCsvConverter
import by.itechart.sftp.SftpConnection

class RetrievalService(
                        val sftpConnection: SftpConnection.type = SftpConnection
                      ) {
  def getPaymentFile(fileName: String): Notice = {
    checkFormatPaymentFile(fileName, sftpConnection.getPaymentFile(fileName))
  }

  private def checkFormatPaymentFile(fN: String, paymentFile: Notice): Notice = {
    paymentFile match {
      case fileName: CsvPaymentFile => fileName
      case fileName: XlsxPaymentFileName =>
        XlsxToCsvConverter.convert(fileName.name) match {
          case notice: CsvPaymentFile => notice
          case notice: EmptyFile => notice
        }
      case _ => InvalidFileName(fN)
    }
  }
}
