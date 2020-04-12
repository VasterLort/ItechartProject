package by.itechart.service

import by.itechart.action._
import by.itechart.constant.Constant
import by.itechart.dao.{Normalization, Validation}
import by.itechart.date.MyDate
import by.itechart.file.PaymentWriter
import org.json4s.Extraction
import org.json4s.native.JsonMethods.{compact, parse, render}

import scala.util.Try

class ValidationService(
                         private val paymentWriter: PaymentWriter.type = PaymentWriter,
                         private val myDate: MyDate.type = MyDate
                       ) {

  private final val IdentificationNumber = "IDENTIFICATION_NUMBER"
  private final val FirstName = "FIRST_NAME"
  private final val LastName = "LAST_NAME"
  private final val WorkingHours = "WORKING_HOURS"
  private final val GrossAmount = "GROSS_AMOUNT"
  private final val AtAmount = "AT_AMOUNT"
  private final val CompanyName = "COMPANY_NAME"
  private final val RequiredColumns = 0
  private final val OptionalColumns = 1
  private final val NotRequiredColumns = "Missing required columns!!!"
  private final val NotHireDateColumns = "DismissalDate columns cannot be without HireDate columns!!!"
  private final val IdentificationNumberPattern = "\\w{14}"
  private final val NumberOfColumnsRequired = 9

  def getValidatedPayments(flow: List[Normalization]): Notice = {
    val validationResult = validatePayments(flow).map {
      case notice: PaymentForValidating => checkValues(notice)
      case notice: PaymentForReporting =>
        paymentWriter.writeFile(notice) match {
          case _: Try[SuccessfulSave] => SuccessfulSave()
          case _: Try[UnsuccessfulSave] => UnsuccessfulSave()
        }
    }

    validationResult.filter(_.isInstanceOf[PaymentForValidating]) match {
      case payments if payments.isEmpty => FailureValidation()
      case payments => ValidatedPayments(payments.map(_.asInstanceOf[PaymentForValidating].payment))
    }
  }

  private def validatePayments(flow: List[Normalization]): List[Notice] = {
    implicit val formats = org.json4s.DefaultFormats

    flow.indices.map { index =>
      checkColumnSize(parse(compact(render(flow(index).content))).extract[Map[String, String]]) match {
        case notice: CorrectColumnsValidationState =>
          PaymentForValidating(Validation(
            flow(index).flowId,
            flow(index).fileName,
            flow(index).companyName,
            flow(index).departmentName,
            flow(index).payDate,
            parse(compact(render(Extraction.decompose(notice.payment)))),
            MyDate.getCurrentDate()
          ))
        case notice: FailedValidation =>
          sendReportAboutProblem(flow(index).flowId, flow(index).fileName, flow(index).companyName, flow(index).departmentName, flow(index).payDate, notice.message, 0)
      }
    }.toList
  }

  private def checkColumnSize(payment: Map[String, String]): Notice = {
    payment match {
      case _ if payment.size == NumberOfColumnsRequired => checkRequiredPaymentColumns(payment, RequiredColumns)
      case _ if payment.size > NumberOfColumnsRequired => checkRequiredPaymentColumns(payment, OptionalColumns)
      case _ => FailedValidation(NotRequiredColumns)

    }
  }

  private def checkRequiredPaymentColumns(payment: Map[String, String], action: Int): Notice = {
    payment match {
      case _ if payment.contains(IdentificationNumber) &&
        payment.contains(FirstName) && payment.contains(LastName) &&
        payment.contains(Constant.BirthDate) && payment.contains(WorkingHours) &&
        payment.contains(GrossAmount) && payment.contains(AtAmount) &&
        payment.contains(Constant.PayDate) && payment.contains(CompanyName) => action match {
        case _ if action == RequiredColumns => CorrectColumnsValidationState(payment)
        case _ if action == OptionalColumns => checkOptionalPaymentColumns(payment)
      }
      case _ => FailedValidation(NotRequiredColumns)
    }
  }

  private def checkOptionalPaymentColumns(payment: Map[String, String]): Notice = {
    payment match {
      case _ if payment.contains(Constant.DismissalDate) && !payment.contains(Constant.HireDate) => FailedValidation(NotHireDateColumns)
      case _ if payment.contains(Constant.DismissalDate) && payment.contains(Constant.HireDate) => CorrectColumnsValidationState(payment)
      case _ => CorrectColumnsValidationState(payment)
    }
  }

  private def checkValues(notice: PaymentForValidating): Notice = {
    implicit val formats = org.json4s.DefaultFormats

    val payment = notice.payment
    val paymentMap = parse(compact(render(payment.content))).extract[Map[String, String]]

    val checkedValues = paymentMap.map {
      case values if (values._1 == Constant.WorkingHours || values._1 == Constant.GrossAmount || values._1 == Constant.AtAmount || values._1 == Constant.Gender) &&
        values._2 == Constant.FalseStatement =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case values if values._1 == IdentificationNumber && !values._2.matches(IdentificationNumberPattern) =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case values if values._1 == Constant.BirthDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getTheEarliestDate) ||
        myDate.getConvertedDate(values._2).isAfter(myDate.getTheLatestDate)) =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case values if values._1 == Constant.HireDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getConvertedDate(paymentMap(Constant.BirthDate)))) =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case values if values._1 == Constant.DismissalDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getConvertedDate(paymentMap(Constant.HireDate)))) =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case values if values._1 == Constant.GrossAmount && values._2.toInt < paymentMap(Constant.AtAmount).toInt =>
        sendReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, 1)
      case _ => CorrectValue()
    }.toList.filter(!_.isInstanceOf[CorrectValue])

    checkedValues match {
      case _ if checkedValues.isEmpty => notice
      case _ => FailureValidation()
    }
  }

  private def sendReportAboutProblem(flowId: String, fileName: String, companyName: String, departmentName: String, payDate: String, problem: String, action: Int): Notice = {
    val failedValidation = action match {
      case _ if action == 0 => FailedValidation(problem)
      case _ if action == 1 => FailedValidation(s"Column with name $problem doesn't have the correct value")
    }

    paymentWriter.writeFile(
      PaymentForReporting(
        flowId,
        fileName,
        companyName,
        departmentName,
        payDate,
        failedValidation
      )
    ) match {
      case _: Try[SuccessfulSave] => SuccessfulSave()
      case _: Try[UnsuccessfulSave] => UnsuccessfulSave()
    }
  }
}
