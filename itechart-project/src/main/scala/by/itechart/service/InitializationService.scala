package by.itechart.service

import by.itechart.action.Notice
import by.itechart.sftp.SftpConnection

class InitializationService(
                             private val sftpConnection: SftpConnection.type = SftpConnection
                           ) {
  def getPaymentFilenames(): Notice = {
    sftpConnection.getPaymentFileName
  }
}
