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
                             private val jsonConverter: CsvToJsonConverter = new CsvToJsonConverter(),
                             private val myDate: MyDate.type = MyDate
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
    myDate.convert(keys(KeyId.payDateId.id).trim) match {
      case payDate: CorrectDate =>
        TransformedPayments(List(
          Transformation(
            flow.flowId,
            flow.fileName,
            keys(KeyId.companyId.id).trim,
            keys(KeyId.departmentId.id).trim,
            payDate.date,
            json,
            MyDate.getCurrentDate())
        ))
      case _ => IncorrectDate()
    }
  }

  private def transformListPayments(flow: Retrieval, payments: List[Payments]): Notice = {
    val result = (Constant.StartIndex until payments.length).map { i =>
      myDate.convert(payments(i).payDate.trim) match {
        case payDate: CorrectDate =>
          PreparedTransformedPayment(
            Transformation(
              flow.flowId,
              flow.fileName,
              payments(i).companyName.trim,
              payments(i).departmentName.trim,
              payDate.date,
              payments(i).columns,
              MyDate.getCurrentDate())
          )
        case payDate if payDate.isInstanceOf[IncorrectDate] &&
          payments(i).companyName.trim.nonEmpty &&
          payments(i).departmentName.trim.nonEmpty =>
          PreparedTransformedPayment(
            Transformation(
              flow.flowId,
              flow.fileName,
              payments(i).companyName.trim,
              payments(i).departmentName.trim,
              Constant.FalseStatement,
              payments(i).columns,
              MyDate.getCurrentDate())
          )
        case _ => EmptyRow()
      }
    }.toList.filter(_.isInstanceOf[PreparedTransformedPayment])

    result match {
      case list if list.isEmpty => FailureTransformation()
      case list => TransformedPayments(list.map(value => value.asInstanceOf[PreparedTransformedPayment].payment))
    }
  }
}
