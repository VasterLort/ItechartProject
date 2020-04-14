package by.itechart.service

import by.itechart.action._
import by.itechart.conf.DictionaryConf
import by.itechart.constant.Constant
import by.itechart.dao.{Normalization, Transformation}
import by.itechart.date.MyDate
import by.itechart.dictionary.Dictionary
import org.json4s.native.JsonMethods.{compact, render, _}
import org.json4s.{DefaultFormats, Extraction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class NormalizationService(
                            private val dictionary: Dictionary = new Dictionary(),
                            private val myDate: MyDate.type = MyDate
                          ) {

  def getNormalizedPayment(flow: List[Transformation]): Future[Notice] = {
    checkDictionary(flow)
  }

  private def checkDictionary(flow: List[Transformation]): Future[Notice] = {
    dictionary.getDictionary().map {
      case notice: PreparedDictionary => normalizePayment(notice.dictionary, flow)
    }
  }

  private def normalizePayment(dictionary: Map[String, String], flow: List[Transformation]): Notice = {
    implicit val formats = org.json4s.DefaultFormats

    val res = flow.map { payment =>
      val map = parse(compact(render(payment.content))).extract[Map[String, String]]
      updateColumn(map, dictionary) match {
        case updatedMap if updatedMap.isEmpty => FailureNormalization()
        case updatedMap =>
          updateValue(updatedMap, dictionary, payment.flowId, payment.fileName, payment.companyName, payment.departmentName, payment.payDate)
      }
    }

    preparePayments(res)
  }

  private def updateColumn(row: Map[String, String], dictionary: Map[String, String]): Map[String, String] = {
    row.map {
      case r if dictionary.contains(r._1) => dictionary(r._1) -> r._2
      case r => Constant.FalseStatement -> r._2
    }.filter(!_._1.contains(Constant.FalseStatement))
  }

  private def updateValue(row: Map[String, String], dictionary: Map[String, String], flowId: String, fileName: String, companyName: String, departmentName: String, payDate: String): Notice = {
    val res = row.map {
      case r if r._1 == Constant.Gender =>
        r._2 match {
          case gender if dictionary.contains(gender) =>
            r._1 -> dictionary(gender)
          case _ => r._1 -> Constant.FalseStatement
        }
      case r if r._1 == Constant.WorkingHours ||
        r._1 == Constant.GrossAmount ||
        r._1 == Constant.AtAmount =>
        r._2 match {
          case number if Try(number.toInt).isSuccess =>
            r._1 -> number
          case _ => r._1 -> Constant.FalseStatement
        }
      case r if r._1 == Constant.BirthDate ||
        r._1 == DictionaryConf.configValues.payDate ||
        r._1 == Constant.HireDate ||
        r._1 == Constant.DismissalDate =>
        r._2 match {
          case anyDate if myDate.convert(anyDate).isInstanceOf[CorrectDate] =>
            r._1 -> myDate.convert(anyDate).asInstanceOf[CorrectDate].date
          case _ => r._1 -> Constant.FalseStatement
        }
      case r => r._1 -> r._2
    }


    NormalizedValue(res, flowId, fileName, companyName, departmentName, payDate)
  }

  private def preparePayments(normalizedPayment: List[Notice]): Notice = {
    implicit val formats = DefaultFormats

    val correctPayments = normalizedPayment
      .filter(_.isInstanceOf[NormalizedValue])
      .asInstanceOf[List[NormalizedValue]]

    correctPayments match {
      case value if value.nonEmpty =>
        val payments = value.map { row =>
          val json = parse(compact(render(Extraction.decompose(row.values))))
          Normalization(
            row.flowId,
            row.fileName,
            row.companyName,
            row.departmentName,
            row.payDate,
            json,
            MyDate.getCurrentDate())
        }
        NormalizedPayments(payments)
      case _ => FailureNormalization()
    }
  }
}


