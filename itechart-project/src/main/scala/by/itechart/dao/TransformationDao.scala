package by.itechart.dao

import by.itechart.database.DatabaseConfig
import by.itechart.database.MyPostgresProfile.api._
import org.json4s.JValue
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Transformation(
                           flowId: String,
                           fileName: String,
                           companyName: String,
                           departmentName: String,
                           payDate: String,
                           content: JValue,
                           creationDate: String,
                           recordId: Long = 0L
                         )

class TransformationDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val scheme = TableQuery[TransformationTable]

  private def insert(flow: Transformation): Future[Transformation] = {
    db.run(scheme
      .filter(payment => payment.flowId === flow.flowId &&
        payment.companyName === flow.companyName &&
        payment.departmentName === flow.departmentName &&
        payment.payDate === flow.payDate)
      .delete
      .andThen((scheme returning scheme.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow))
  }

  def insertAll(flows: List[Transformation]): Future[Seq[Transformation]] = {
    Future.sequence(flows.map { flow =>
      insert(flow)
    })
  }

  def getById(flowId: String): Future[Seq[Transformation]] = {
    db.run(scheme.filter(flow => flow.flowId === flowId).result)
  }

  def getByKeys(flowId: String, companyName: String, departmentName: String, payDate: String): Future[Seq[Transformation]] = {
    db.run(scheme.filter(flow =>
      flow.flowId === flowId && flow.companyName === companyName && flow.departmentName === departmentName && flow.payDate === payDate)
      .result)
  }

  private class TransformationTable(tag: Tag) extends Table[Transformation](tag, "flow_transformation") {

    def recordId = column[Long]("record_id", O.PrimaryKey, O.AutoInc)

    def flowId = column[String]("flow_id")

    def fileName = column[String]("file_name")

    def companyName = column[String]("company_name")

    def departmentName = column[String]("department_name")

    def payDate = column[String]("pay_date")

    def content = column[JValue]("content")

    def creationDate = column[String]("creation_date")

    def * = (flowId, fileName, companyName, departmentName, payDate, content, creationDate, recordId) <> (Transformation.tupled, Transformation.unapply)
  }

}