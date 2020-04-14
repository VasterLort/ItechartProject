package by.itechart.action

import by.itechart.conversion.Payments
import by.itechart.dao._
import org.json4s.JValue

sealed trait Notice

case class SuccessfulRequest(flow: Flow, message: String = "Request was completed!!!") extends Notice

case class FailureRequest(message: String = "Error!!!") extends Notice

case class SuccessfulRequestForRetrieval(flow: Retrieval, message: String = "Request was completed!!!") extends Notice

case class FailureRetrieval(message: String = "Request was failed!!!") extends Notice

case class SuccessfulRequestForTransformation(flow: Seq[Transformation], message: String = "Request was completed!!!") extends Notice

case class FailureTransformation(message: String = "Transformation was failed!!!") extends Notice

case class SuccessfulRequestForNormalization(flow: Seq[Normalization], message: String = "Request was completed!!!") extends Notice

case class FailureNormalization(message: String = "Normalization was failed!!!") extends Notice

case class EmptyFile(fileName: String, message: String = "File is empty") extends Notice

case class EmptyFolder(message: String) extends Notice

case class NotEmptyFolderSuccessful(message: String) extends Notice

case class NotEmptyFolderFailure(results: String) extends Notice

case class InvalidFileName(fileName: String, message: String = "File name is invalid") extends Notice

case class CsvPaymentFileName(name: String) extends Notice

case class XlsxPaymentFileName(name: String) extends Notice

case class CsvPaymentFile(content: String, fileName: String) extends Notice

case class XlsxPaymentFile(content: String, fileName: String) extends Notice

case class PaymentFileAction(name: String) extends Notice

case class PaymentFileNameAction() extends Notice

case class PaymentFileName(name: java.util.Vector[_]) extends Notice

case class ConversionPaymentsSucceed(payments: List[Payments]) extends Notice

case class ConversionPaymentSucceed(json: JValue, keys: Array[String]) extends Notice

case class ConversionError(message: String = "Conversion error Csv to Json!!!") extends Notice

case class CorrectDate(date: String) extends Notice

case class IncorrectDate() extends Notice

case class CorrectKeys(value: Map[String, String]) extends Notice

case class IncorrectKeys(message: String = "Incorrect Keys!!!") extends Notice

case class CorrectDictionary(dictionary: List[Dictionary]) extends Notice

case class PreparedDictionary(dictionary: Map[String, String]) extends Notice

case class IncorrectDictionary(message: String = "Dictionary is empty!!!") extends Notice

case class SinglePayment() extends Notice

case class SeveralPayments() extends Notice

case class TransformedPayments(payments: List[Transformation]) extends Notice

case class PreparedTransformedPayment(payment: Transformation) extends Notice

case class NormalizedValue(values: Map[String, String], flowId: String, fileName: String, companyName: String, departmentName: String, payDate: String) extends Notice

case class NormalizedPayments(payments: List[Normalization]) extends Notice

case class FailedValidation(message: String) extends Notice

case class CorrectColumnsValidationState(payment: Map[String, String]) extends Notice

case class PaymentForValidating(payment: Validation) extends Notice

//case class PaymentForReporting(description: String) extends Notice

case class PaymentForReporting(description: Map[String, String]) extends Notice

case class SuccessfulSave() extends Notice

case class UnsuccessfulSave() extends Notice

case class ValidatedPayments(payments: List[Validation]) extends Notice

case class SuccessfulRequestForValidation(flow: Seq[Validation], message: String = "Request was completed!!!") extends Notice

case class SuccessfulRequestForLoad(flow: Seq[Load], message: String = "Request was completed!!!") extends Notice

case class FailureValidation(message: String = "Validation was failed!!!") extends Notice

case class SuccessfulValidation(message: String = "Successful!!!") extends Notice

case class FailureValidationList(messages: List[PaymentForReporting]) extends Notice

case class CorrectValue() extends Notice

case class PreparedPaymentsForLoading(payments: List[Load]) extends Notice

case class FailureLoading() extends Notice

case class Successful() extends Notice

case class EmptyRow() extends Notice