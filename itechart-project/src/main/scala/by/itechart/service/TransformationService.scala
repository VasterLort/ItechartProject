package by.itechart.service

import by.itechart.action.Notice
import by.itechart.conversion.CsvToJsonConverter
import by.itechart.dao.Retrieval

class TransformationService {
  def getTransformedData(flow: Retrieval): Notice = {
    checkFileName(flow)
  }

  private def checkFileName(flow: Retrieval): Notice = {
    flow.fileName match {
      case name if name.matches("\\w+_\\w+_\\d{8}.(csv|xlst)") => CsvToJsonConverter.convertSinglePayment(flow)
      case _ => CsvToJsonConverter.convertMultiplePayments(flow)
    }
  }
}
