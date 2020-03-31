package by.itechart.action

import by.itechart.dao.{Flow, Retrieval}

sealed trait Notice

case class SuccessfulRequest(flow: Flow, message: String = "Request was completed!!!") extends Notice

case class FailureRequest(message: String = "Error!!!") extends Notice

case class SuccessfulRequestForRetrieval(flow: Retrieval, message: String = "Request was completed!!!") extends Notice

case class FailureRetrieval(message: String = "Request was completed!!!") extends Notice

case class EmptyFile(message: String = "File is empty") extends Notice

case class EmptyFolder(message: String = "Folder is empty") extends Notice

case class InvalidFileName(message: String = "File name is invalid") extends Notice

case class CsvPaymentFileName(name: String) extends Notice

case class XlsxPaymentFileName(name: String) extends Notice

case class CsvPaymentFile(content: String, fileName: String) extends Notice

case class XlsxPaymentFile(content: String, fileName: String) extends Notice

case class PaymentFileAction(name: String) extends Notice

case class PaymentFileNameAction() extends Notice

case class PaymentFileName(name: java.util.Vector[_]) extends Notice