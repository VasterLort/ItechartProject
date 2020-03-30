package by.itechart.dao

import by.itechart.database.DatabaseConfig
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Flow(
                 flowId: String,
                 fileName: String,
                 statusId: Long,
                 statusDate: String,
                 recordId: Long = 0L
               )

class FlowDao(
               private val dbProvider: DatabaseConfig.type = DatabaseConfig
             ) {
  private val db = dbProvider.db
  private val flows = TableQuery[FlowTable]

  def insert(flow: Flow): Future[Flow] = {
    db.run((flows returning flows.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow)
  }

  def getById(flowId: String, statusId: Long): Future[Option[Flow]] = {
    db.run(flows.filter(flow => flow.flowId === flowId && flow.statusId === statusId).result.headOption)
  }

  private class FlowTable(tag: Tag) extends Table[Flow](tag, "flow") {
    def flowId = column[String]("flow_id", O.PrimaryKey)

    def fileName = column[String]("file_name")

    def statusId = column[Long]("status_id")

    def statusDate = column[String]("status_date")

    def recordId = column[Long]("record_id", O.AutoInc)

    def * = (flowId, fileName, statusId, statusDate, recordId) <> (Flow.tupled, Flow.unapply)
  }

}