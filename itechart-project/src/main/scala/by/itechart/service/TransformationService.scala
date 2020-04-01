package by.itechart.service

import by.itechart.action.{MultiplePayments, Notice, SinglePayment}
import by.itechart.dao.Retrieval

class TransformationService {
  def getTransformedData(flow: Retrieval): Notice = {
    checkFileName(flow.fileName)
  }

  private def checkFileName(fileName: String): Notice = {
    fileName match {
      case name if name.matches("\\w+_\\w+_\\d{8}.(csv|xlst)") => SinglePayment()
      case _ => MultiplePayments()
    }
  }
}
