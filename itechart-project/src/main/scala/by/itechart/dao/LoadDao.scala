package by.itechart.dao

import java.time.LocalDate

import by.itechart.database.DatabaseConfig
import by.itechart.database.MyPostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Load(
                 flowId: String,
                 fileName: String,
                 companyName: String,
                 departmentName: String,
                 payDate: String,
                 identificationNumber: String,
                 firstName: String,
                 lastName: String,
                 birthDate: LocalDate,
                 workingHours: Int,
                 grossAmount: Int,
                 atAmount: Int,
                 hireDate: Option[LocalDate],
                 dismissalDate: Option[LocalDate],
                 gender: Option[String],
                 postalCode: Option[String],
                 creationDate: String,
                 recordId: Long = 0L
               )

class LoadDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val scheme = TableQuery[LoadTable]

  private def insert(flow: Load): Future[Load] = {
    db.run(scheme
      .filter(payment => payment.flowId === flow.flowId &&
        payment.companyName === flow.companyName &&
        payment.departmentName === flow.departmentName &&
        payment.payDate === flow.payDate)
      .delete
      .andThen((scheme returning scheme.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow))
  }

  def insertAll(flows: List[Load]): Future[Seq[Load]] = {
    Future.sequence(flows.map { flow =>
      insert(flow)
    })
  }

  def getById(flowId: String): Future[Seq[Load]] = {
    db.run(scheme.filter(flow => flow.flowId === flowId).result)
  }

  def getByKeys(flowId: String, companyName: String, departmentName: String, payDate: String): Future[Seq[Load]] = {
    db.run(scheme.filter(flow =>
      flow.flowId === flowId && flow.companyName === companyName && flow.departmentName === departmentName && flow.payDate === payDate)
      .result)
  }

  private class LoadTable(tag: Tag) extends Table[Load](tag, "flow_load") {

    def recordId = column[Long]("record_id", O.PrimaryKey, O.AutoInc)

    def flowId = column[String]("flow_id")

    def fileName = column[String]("file_name")

    def companyName = column[String]("company_name")

    def departmentName = column[String]("department_name")

    def payDate = column[String]("pay_date")

    def identificationNumber = column[String]("identification_number")

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def birthDate = column[LocalDate]("birth_date")

    def workingHours = column[Int]("working_hours")

    def grossAmount = column[Int]("gross_amount")

    def atAmount = column[Int]("at_amount")

    def hireDate = column[Option[LocalDate]]("hire_date")

    def dismissalDate = column[Option[LocalDate]]("dismissal_date")

    def gender = column[Option[String]]("gender")

    def postalCode = column[Option[String]]("postal_code")

    def creationDate = column[String]("creation_date")

    def * = (flowId, fileName, companyName, departmentName, payDate, identificationNumber,
      firstName, lastName, birthDate, workingHours, grossAmount, atAmount, hireDate, dismissalDate, gender,
      postalCode, creationDate, recordId) <> (Load.tupled, Load.unapply)
  }

}
