package by.itechart.conversion

import by.itechart.action._
import by.itechart.conf.DictionaryConf
import by.itechart.constant.Constant
import by.itechart.dao.Retrieval
import by.itechart.dictionary.Dictionary
import org.json4s.native.JsonMethods.{compact, render, _}
import org.json4s.{DefaultFormats, Extraction, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CsvToJsonConverter(
                          private val dictionary: Dictionary = new Dictionary()
                        ) {
  implicit val formats = DefaultFormats

  def convert(flow: Retrieval, fileContent: List[Array[String]], notice: Notice): Future[Notice] = {
    val head = fileContent(Constant.StartIndex)

    notice match {
      case _: SinglePayment => Future.successful(convertSinglePaymentToJson(fileContent, flow.fileName, head))
      case _: SeveralPayments => convertSeveralPaymentToJson(fileContent, head)
    }
  }


  private def getKeys(head: Array[String]): Future[Notice] = {
    dictionary.getFileNameKeys().map {
      case content: PreparedDictionary => {
        val keys = head.toList.map {
          case name if content.dictionary.contains(name) => content.dictionary(name) -> name
          case name => name -> Constant.FalseStatement
        }.filter(!_._2.contentEquals(Constant.FalseStatement)).toMap
        checkKeys(keys)
      }
      case _ => IncorrectKeys()
    }
  }

  private def checkKeys(keys: Map[String, String]): Notice = {
    keys match {
      case k if k.exists(_._1 == DictionaryConf.configValues.company)
        && k.exists(_._1 == DictionaryConf.configValues.department)
        && k.exists(_._1 == DictionaryConf.configValues.payDate) => CorrectKeys(keys)
      case _ => IncorrectKeys()
    }
  }

  private def preparePaymentFile(fileContent: List[Array[String]], head: Array[String]): List[Map[String, String]] = {
    fileContent
      .drop(Constant.HeadIndex)
      .map { row =>
        row.indices.map { i =>
          head(i) -> row(i)
        }.toMap
      }
  }

  private def convertSinglePaymentToJson(fileContent: List[Array[String]], fileName: String, head: Array[String]): Notice = {
    val preparedData = preparePaymentFile(fileContent, head)
    ConversionPaymentSucceed(parse(compact(render(Extraction.decompose(preparedData(Constant.StartIndex))))), fileName.split(Constant.FileNameDelimiter))
  }

  private def convertSeveralPaymentToJson(fileContent: List[Array[String]], head: Array[String]): Future[Notice] = {
    getKeys(head).map {
      case keys: CorrectKeys => {
        val jsonList = preparePaymentFile(fileContent, head).map { row =>
          parse(compact(render(Extraction.decompose(row))))
        }
        ConversionPaymentsSucceed(jsonList, keys.value)
      }
      case _: IncorrectKeys => ConversionError()
    }
  }
}
