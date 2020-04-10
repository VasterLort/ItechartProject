package by.itechart.service

import by.itechart.action._
import by.itechart.constant.{Constant, KeyId}
import by.itechart.conversion.{CsvToJsonConverter, Payments}
import by.itechart.dao.{Retrieval, Transformation}
import by.itechart.date.MyDate
import org.json4s.JsonAST.JValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransformationService(
                             private val jsonConverter: CsvToJsonConverter = new CsvToJsonConverter()
                           ) {

  private final val SinglePaymentLength = 2
  private final val SingleFileNameRegex = "\\w+_\\w+_\\d{8}.(csv|xlst)"

  def getTransformedData(flow: Retrieval): Future[Notice] = {
    checkFileName(flow).map {
      case notice: ConversionPaymentSucceed => transformSinglePayment(flow, notice.json, notice.keys)
      case notice: ConversionPaymentsSucceed => transformListPayments(flow, notice.payments)
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
      case name if name.matches(SingleFileNameRegex) && fileContent.length == SinglePaymentLength =>
        jsonConverter.convert(flow, fileContent, SinglePayment())
      case _ => jsonConverter.convert(flow, fileContent, SeveralPayments())
    }
  }

  private def transformSinglePayment(flow: Retrieval, json: JValue, keys: Array[String]): Notice = {
    val result = List(Transformation(flow.flowId, flow.fileName, keys(KeyId.companyId.id), keys(KeyId.departmentId.id), keys(KeyId.payDateId.id), json, MyDate.getCurrentDate()))
    TransformedPayments(result)
  }

  private def transformListPayments(flow: Retrieval, payments: List[Payments]): Notice = {
    val result = (Constant.StartIndex until payments.length).map { i =>
      Transformation(
        flow.flowId,
        flow.fileName,
        payments(i).companyName,
        payments(i).departmentName,
        payments(i).payDate,
        payments(i).columns,
        MyDate.getCurrentDate())
    }.toList

    TransformedPayments(result)
  }
}
