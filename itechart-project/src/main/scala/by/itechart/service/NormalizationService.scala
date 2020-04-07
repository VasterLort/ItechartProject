package by.itechart.service

import by.itechart.action._
import by.itechart.constant.Constant
import by.itechart.dao.{Normalization, Transformation}
import by.itechart.date.MyDate
import by.itechart.dictionary.Dictionary
import org.json4s.native.JsonMethods.{compact, render, _}
import org.json4s.{DefaultFormats, Extraction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NormalizationService(
                            private val dictionary: Dictionary = new Dictionary()
                          ) {

  private final val Gender = "GENDER"

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
    row match {
      case _ if row.contains(Gender) =>
        row(Gender) match {
          case gender if dictionary.contains(gender) => NormalizedValue(row.updated(Gender, dictionary(gender)), flowId, fileName, companyName, departmentName, payDate)
          case _ => NormalizedValue(row.updated(Gender, Constant.FalseStatement), flowId, fileName, companyName, departmentName, payDate)
        }
      case _ => FailureNormalization()
    }
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


