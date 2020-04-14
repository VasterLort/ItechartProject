package by.itechart.service

import by.itechart.action.{ValidatedPayments, _}
import by.itechart.conf.DictionaryConf
import by.itechart.constant.Constant
import by.itechart.dao.{Normalization, Validation}
import by.itechart.date.MyDate
import org.json4s.native.JsonMethods.{compact, parse, render}
import org.json4s.{DefaultFormats, Extraction}

class ValidationService(
                         private val myDate: MyDate.type = MyDate
                       ) {

  private final val IdentificationNumber = "IDENTIFICATION_NUMBER"
  private final val FirstName = "FIRST_NAME"
  private final val LastName = "LAST_NAME"
  private final val WorkingHours = "WORKING_HOURS"
  private final val GrossAmount = "GROSS_AMOUNT"
  private final val AtAmount = "AT_AMOUNT"
  private final val RequiredColumns = 0
  private final val OptionalColumns = 1
  private final val ColumnAction = 0
  private final val ValueAction = 1
  private final val NotRequiredColumns = "Missing required columns!!!"
  private final val NotHireDateColumns = "DismissalDate columns cannot be without HireDate columns!!!"
  private final val IdentificationNumberLength = 14
  private final val NumberOfColumnsRequired = 9

  def getValidatedPayments(flow: List[Normalization]): Notice = {
    validatePayments(flow).map {
      case notice: PaymentForValidating => checkValues(notice)
      case notice: PaymentForReporting => notice
    } match {
      case payments if payments.filter(!_.isInstanceOf[PaymentForValidating]).isEmpty =>
        ValidatedPayments(payments.map(_.asInstanceOf[PaymentForValidating].payment))
      case payments => prepareReport(payments.filter(!_.isInstanceOf[PaymentForValidating]))
    }
  }

  private def prepareReport(notices: List[Notice]): FailureValidationList = {
    val list = notices.flatMap {
      case notice: PaymentForReporting => List(notice)
      case notice: FailureValidationList => notice.messages
    }

    FailureValidationList(list)
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
          makeReportAboutProblem(flow(index).flowId, flow(index).fileName, flow(index).companyName, flow(index).departmentName, flow(index).payDate, notice.message, ColumnAction)
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
        payment.contains(DictionaryConf.configValues.payDate) && payment.contains(DictionaryConf.configValues.company) => action match {
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
      case values if (values._1 == Constant.Gender || values._1 == Constant.WorkingHours ||
        values._1 == DictionaryConf.configValues.department || values._1 == Constant.GrossAmount ||
        values._1 == Constant.AtAmount || values._1 == DictionaryConf.configValues.payDate ||
        values._1 == DictionaryConf.configValues.company) && (values._2 == Constant.FalseStatement || values._2 == "") =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case values if values._1 == Constant.IdentificationNumber && values._2.length != IdentificationNumberLength =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case values if values._1 == Constant.BirthDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getTheEarliestDate) ||
        myDate.getConvertedDate(values._2).isAfter(myDate.getTheLatestDate)) =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case values if values._1 == Constant.HireDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getConvertedDate(paymentMap(Constant.BirthDate)))) =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case values if values._1 == Constant.DismissalDate && (values._2 == Constant.FalseStatement ||
        myDate.getConvertedDate(values._2).isBefore(myDate.getConvertedDate(paymentMap(Constant.HireDate)))) =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case values if values._1 == Constant.GrossAmount && values._2.toInt < paymentMap(Constant.AtAmount).toInt =>
        makeReportAboutProblem(payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate, values._1, ValueAction)
      case _ => CorrectValue()
    }.toList

    checkedValues match {
      case _ if checkedValues.filter(!_.isInstanceOf[CorrectValue]).isEmpty => notice
      case _ => FailureValidationList(checkedValues.filter(_.isInstanceOf[PaymentForReporting]).map(_.asInstanceOf[PaymentForReporting]))
    }
  }

  private def makeReportAboutProblem(flowId: String, fileName: String, companyName: String, departmentName: String, payDate: String, problem: String, action: Int): PaymentForReporting = {
    implicit val formats = DefaultFormats

    val failedValidation = action match {
      case _ if action == ColumnAction => FailedValidation(problem)
      case _ if action == ValueAction => FailedValidation(s"Column with name $problem doesn't have the correct value")
    }

    PaymentForReporting(
      Map(
        "flowId" -> flowId,
        "fileName" -> fileName,
        "companyName" -> companyName,
        "departmentName" -> departmentName,
        "payDate" -> payDate,
        "problem" -> failedValidation.message
      )
    )
  }
}
