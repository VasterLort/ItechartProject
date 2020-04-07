package by.itechart.service

import by.itechart.action._
import by.itechart.conf.DictionaryConf
import by.itechart.constant.{Constant, KeyId}
import by.itechart.conversion.CsvToJsonConverter
import by.itechart.dao.{Retrieval, Transformation}
import by.itechart.date.MyDate
import org.json4s.JsonAST.JValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransformationService(
                             private val jsonConverter: CsvToJsonConverter = new CsvToJsonConverter()
                           ) {
  def getTransformedData(flow: Retrieval): Future[Notice] = {
    checkFileName(flow).map {
      case notice: ConversionPaymentSucceed => transformSinglePayment(flow, notice.json, notice.keys)
      case notice: ConversionPaymentsSucceed => transformListPayments(flow, notice.json, notice.keys)
      case _ => FailureTransformation()
    }
  }

  private def checkFileName(flow: Retrieval): Future[Notice] = {
    val fileContent =
      flow.content
        .split(Constant.RowDelimiterOfFile)
        .map(_.split(Constant.ContentDelimiterOfFile))
        .toList

    flow.fileName match {
      case _ if fileContent.length <= Constant.HeadIndex => Future.successful(FailureTransformation())
      case name if name.matches(Constant.SingleFileNameRegex) && fileContent.length == Constant.SinglePaymentLength =>
        jsonConverter.convert(flow, fileContent, SinglePayment())
      case _ => jsonConverter.convert(flow, fileContent, SeveralPayments())
    }
  }

  private def transformSinglePayment(flow: Retrieval, json: JValue, keys: Array[String]): Notice = {
    val result = List(Transformation(flow.flowId, flow.fileName, keys(KeyId.companyId.id), keys(KeyId.departmentId.id), keys(KeyId.payDateId.id), json, MyDate.getCurrentDate()))
    TransformedPayments(result)
  }

  private def transformListPayments(flow: Retrieval, json: List[JValue], keys: Map[String, String]): Notice = {
    val result = (Constant.StartIndex until json.length).map { i =>
      Transformation(
        flow.flowId,
        flow.fileName,
        keys(DictionaryConf.configValues.company),
        keys(DictionaryConf.configValues.department),
        keys(DictionaryConf.configValues.payDate),
        json(i),
        MyDate.getCurrentDate())
    }.toList

    TransformedPayments(result)
  }
}
