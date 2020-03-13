package by.itechart.entity

import by.itechart.config.DatabaseConfig
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

case class Flow(
                 flowId: Long,
                 statusId: Long,
                 statusDate: String,
                 recordId: Long
               )

class FlowDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val bikes = TableQuery[FlowTable]

  private class FlowTable(tag: Tag) extends Table[Flow](tag, "flow") {
    def flowId = column[Long]("flow_id", O.PrimaryKey)

    def statusId = column[Long]("status_id")

    def statusDate = column[String]("status_date")

    def recordId = column[Long]("record_id")

    def * = (flowId, statusId, statusDate, recordId) <> (Flow.tupled, Flow.unapply)
  }

}