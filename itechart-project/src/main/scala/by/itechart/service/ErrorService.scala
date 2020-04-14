package by.itechart.service

import by.itechart.action.{EmptyFile, FailureValidationList, InvalidFileName, Notice}
import by.itechart.constant.Constant
import org.json4s.native.JsonMethods.{pretty, render}
import org.json4s.{DefaultFormats, Extraction}

class ErrorService {
  private final val FlowId = "flowId"

  def getMergedErrors(seq: Seq[Notice]): String = {
    val seqFailureValidationList = prepareFailureValidationList(seq.filter(_.isInstanceOf[FailureValidationList]).map(_.asInstanceOf[FailureValidationList]))
    val seqEmptyFile =
      prepareSingleError(seq
        .filter(_.isInstanceOf[EmptyFile])
        .map(_.asInstanceOf[EmptyFile])
        .map(values => (values.fileName, values.message)))

    val seqInvalidFileName =
      prepareSingleError(seq
        .filter(_.isInstanceOf[InvalidFileName])
        .map(_.asInstanceOf[InvalidFileName])
        .map(values => (values.fileName, values.message)))

    val reports = s"$seqInvalidFileName,\n$seqEmptyFile,\n$seqFailureValidationList,\n".replaceAll(s"${Constant.FalseStatement},\n", "")

    reports match {
      case _ if reports.endsWith(",\n") => s"{\nvalidation_summary:[\n${reports.dropRight(3)}}\n]}"
      case _ => s"{\nvalidation_summary:[\n$reports\n]}"
    }
  }

  private def prepareSingleError(seq: Seq[(String, String)]): String = {
    seq match {
      case _ if seq.isEmpty => Constant.FalseStatement
      case _ => seq.map(list => getJsonFormat(Map(
        "fileName" -> list._1,
        "problem" -> list._2
      ))).mkString(Constant.JsonDelimiter)
    }
  }

  private def getJsonFormat(report: Map[String, String]): String = {
    implicit val formats = DefaultFormats
    pretty(render(Extraction.decompose(report)))
  }

  private def prepareFailureValidationList(seq: Seq[FailureValidationList]): String = {
    implicit val formats = DefaultFormats

    seq match {
      case _ if seq.isEmpty => Constant.FalseStatement
      case _ => seq.map { list =>
        val r = list.messages.groupBy(_.description(FlowId))
        pretty(render(Extraction.decompose(r)))
      }.mkString(Constant.JsonDelimiter)
    }
  }

  def getSuccessfulResult(message: String): String = {
    getJsonFormat(Map("message" -> message))
  }
}
