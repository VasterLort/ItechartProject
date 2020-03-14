package by.itechart.dao

import by.itechart.config.DatabaseConfig
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Flow(
                 flowId: Long,
                 statusId: Long,
                 statusDate: String,
                 recordId: Long = 0L
               )

class FlowDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val flows = TableQuery[FlowTable]

  def insert(flow: Flow): Future[Flow] = {
    db.run((flows returning flows.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow)
  }

  private class FlowTable(tag: Tag) extends Table[Flow](tag, "flow") {
    def flowId = column[Long]("flow_id", O.PrimaryKey)

    def statusId = column[Long]("status_id")

    def statusDate = column[String]("status_date")

    def recordId = column[Long]("record_id", O.AutoInc)

    def * = (flowId, statusId, statusDate, recordId) <> (Flow.tupled, Flow.unapply)
  }

}