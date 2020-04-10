package by.itechart.dao

import by.itechart.database.DatabaseConfig
import by.itechart.database.MyPostgresProfile.api._
import org.json4s.JValue
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Validation(
                       flowId: String,
                       fileName: String,
                       companyName: String,
                       departmentName: String,
                       payDate: String,
                       content: JValue,
                       creationDate: String,
                       recordId: Long = 0L
                     )

class ValidationDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val scheme = TableQuery[ValidationTable]

  private def insert(flow: Validation): Future[Validation] = {
    db.run(scheme
      .filter(payment => payment.flowId === flow.flowId &&
        payment.companyName === flow.companyName &&
        payment.departmentName === flow.departmentName &&
        payment.payDate === flow.payDate)
      .delete
      .andThen((scheme returning scheme.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow))
  }

  def insertAll(flows: List[Validation]): Future[Seq[Validation]] = {
    Future.sequence(flows.map { flow =>
      insert(flow)
    })
  }

  def getById(flowId: String): Future[Seq[Validation]] = {
    db.run(scheme.filter(flow => flow.flowId === flowId).result)
  }

  def getByKeys(flowId: String, companyName: String, departmentName: String, payDate: String): Future[Seq[Validation]] = {
    db.run(scheme.filter(flow =>
      flow.flowId === flowId && flow.companyName === companyName && flow.departmentName === departmentName && flow.payDate === payDate)
      .result)
  }

  private class ValidationTable(tag: Tag) extends Table[Validation](tag, "flow_validation") {

    def recordId = column[Long]("record_id", O.PrimaryKey, O.AutoInc)

    def flowId = column[String]("flow_id")

    def fileName = column[String]("file_name")

    def companyName = column[String]("company_name")

    def departmentName = column[String]("department_name")

    def payDate = column[String]("pay_date")

    def content = column[JValue]("content")

    def creationDate = column[String]("creation_date")

    def * = (flowId, fileName, companyName, departmentName, payDate, content, creationDate, recordId) <> (Validation.tupled, Validation.unapply)
  }

}
