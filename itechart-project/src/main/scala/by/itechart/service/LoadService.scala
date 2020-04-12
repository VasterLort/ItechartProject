package by.itechart.service

import java.time.LocalDate

import by.itechart.action.{FailureLoading, Notice, PreparedPaymentsForLoading}
import by.itechart.constant.Constant
import by.itechart.dao.{Load, Validation}
import by.itechart.date.MyDate
import org.json4s.native.JsonMethods.{compact, parse, render}

class LoadService(private val myDate: MyDate.type = MyDate) {
  def parsePayment(flow: List[Validation]): Notice = {
    implicit val formats = org.json4s.DefaultFormats

    val payments = flow.map { payment =>
      val paymentValues = parse(compact(render(payment.content))).extract[Map[String, String]]
      Load(
        payment.flowId,
        payment.fileName,
        payment.companyName,
        payment.departmentName,
        payment.payDate,
        paymentValues(Constant.IdentificationNumber),
        paymentValues(Constant.FirstName),
        paymentValues(Constant.LastName),
        myDate.getConvertedDate(paymentValues(Constant.BirthDate)),
        paymentValues(Constant.WorkingHours).toInt,
        paymentValues(Constant.GrossAmount).toInt,
        paymentValues(Constant.AtAmount).toInt,
        checkOptionalLocalDateColumn(paymentValues, Constant.HireDate),
        checkOptionalLocalDateColumn(paymentValues, Constant.DismissalDate),
        checkOptionalStringColumn(paymentValues, Constant.Gender),
        checkOptionalStringColumn(paymentValues, Constant.PostalCode),
        myDate.getCurrentDate()
      )
    }.filter(_.isInstanceOf[Load])

    payments match {
      case _ if payments.isEmpty => FailureLoading()
      case _ => PreparedPaymentsForLoading(payments)
    }
  }

  private def checkOptionalStringColumn(paymentValues: Map[String, String], columnName: String): Option[String] = {
    columnName match {
      case _ if paymentValues.contains(columnName) => Option(paymentValues(columnName))
      case _ => Option(null)
    }
  }

  private def checkOptionalLocalDateColumn(paymentValues: Map[String, String], columnName: String): Option[LocalDate] = {
    columnName match {
      case _ if paymentValues.contains(columnName) => Option(myDate.getConvertedDate(paymentValues(columnName)))
      case _ => Option(null)
    }
  }

}
