package by.itechart.file

import java.io.{BufferedWriter, File, FileWriter}

import by.itechart.action.{Notice, PaymentForReporting, SuccessfulSave, UnsuccessfulSave}
import by.itechart.conf.GeneralConf

import scala.util.{Failure, Success, Try}

object PaymentWriter {
  def writeFile(notice: PaymentForReporting): Try[Notice] = {
    val fileName = s"${notice.companyName}_${notice.departmentName}_${notice.payDate}.txt"

    Try(new BufferedWriter(new FileWriter(new File(GeneralConf.configValues.resourcePath + fileName), true))).flatMap((bw: BufferedWriter) =>
      Try {
        bw.write(s"FlowId: ${notice.flowId}\n" +
          s"CompanyName: ${notice.companyName}\n" +
          s"DepartmentName: ${notice.departmentName}\n" +
          s"PayDate: ${notice.payDate}\n" +
          s"Problem: ${notice.notice.message}\n\n")
        bw.close()
      } match {
        case Success(_) => {
          Try(SuccessfulSave())
        }
        case Failure(_) =>
          bw.close()
          Try(UnsuccessfulSave())
      }
    ) match {
      case Success(_) => {
        Try(SuccessfulSave())
      }
      case Failure(_) =>
        Try(UnsuccessfulSave())
    }
  }
}
