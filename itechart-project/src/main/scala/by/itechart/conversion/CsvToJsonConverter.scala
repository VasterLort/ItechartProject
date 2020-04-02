package by.itechart.conversion

import by.itechart.action.{ConversionSucceed, _}
import by.itechart.conf.{DictionaryConf, GeneralConf}
import by.itechart.dao.Retrieval
import by.itechart.dictionary.ColumnNameDictionary
import org.json4s.native.JsonMethods.{compact, render, _}
import org.json4s.{DefaultFormats, Extraction, _}

object CsvToJsonConverter {
  implicit val formats = DefaultFormats

  type MultiplePayments = Map[String, Map[String, Map[String, List[Map[String, String]]]]]
  type SinglePayment = List[Map[String, String]]

  def convertSinglePayment(flow: Retrieval): Notice = {
    val fileContent = flow.content.split(GeneralConf.configValues.rowDelimiterOfFile).map(_.split(GeneralConf.configValues.contentDelimiterOfFile)).toList
    val head = fileContent(GeneralConf.configValues.startIndex)

    fileContent match {
      case _ if fileContent.length > GeneralConf.configValues.singlePaymentLength => ConversionError()
      case _ => {
        parse(compact(render(Extraction.decompose(prepareSinglePayment(fileContent, head))))) match {
          case res: JValue => ConversionSucceed(res)
          case _ => ConversionError()
        }
      }
    }
  }

  def convertMultiplePayments(flow: Retrieval): Notice = {
    val fileContent = flow.content.split(GeneralConf.configValues.rowDelimiterOfFile).map(_.split(GeneralConf.configValues.contentDelimiterOfFile)).toList
    val head = fileContent(GeneralConf.configValues.startIndex)

    getKeys(head) match {
      case keys: CorrectKeys => {
        val preparedMultiplePayments = prepareMultiplePayments(fileContent, head, keys)
        parse(compact(render(Extraction.decompose(preparedMultiplePayments)))) match {
          case res: JValue => ConversionSucceed(res)
          case _ => ConversionError()
        }
      }
      case _: IncorrectKeys => ConversionError()
    }
  }

  private def getKeys(head: Array[String]): Notice = {
    val dictionary = ColumnNameDictionary.values.columnName

    val keys = head.toList.map {
      case name if dictionary.contains(name) => dictionary(name) -> name
      case name => name -> GeneralConf.configValues.falseStatement
    }.filter(!_._2.contentEquals(GeneralConf.configValues.falseStatement)).toMap

    checkKeys(keys)
  }

  private def checkKeys(keys: Map[String, String]): Notice = {
    keys match {
      case k if k.exists(_._1 == DictionaryConf.configValues.company)
        && k.exists(_._1 == DictionaryConf.configValues.department)
        && k.exists(_._1 == DictionaryConf.configValues.payDate) => CorrectKeys(keys)
      case _ => IncorrectKeys()
    }
  }

  private def prepareSinglePayment(fileContent: List[Array[String]], head: Array[String]): List[Map[String, String]] = {
    fileContent
      .drop(GeneralConf.configValues.headIndex)
      .map { row =>
        row.indices.map { i =>
          head(i) -> row(i)
        }.toMap
      }
  }

  private def prepareMultiplePayments(fileContent: List[Array[String]], head: Array[String], keys: CorrectKeys): MultiplePayments = {
    fileContent
      .drop(GeneralConf.configValues.headIndex)
      .map { row =>
        row.indices.map { i =>
          head(i) -> row(i)
        }.toMap
      }
      .groupBy(m => m(keys.value(DictionaryConf.configValues.company)))
      .map(ma => ma._1 -> ma._2.groupBy(m => m(keys.value(DictionaryConf.configValues.department))))
      .map(ma => ma._1 -> ma._2.map(m => m._1 -> m._2.groupBy(m => m(keys.value(DictionaryConf.configValues.payDate)))))
  }
}
