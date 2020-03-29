package by.itechart.dao

import by.itechart.config.DatabaseConfig
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Retrieval(
                      flowId: String,
                      fileName: String,
                      content: String,
                      creationDate: String,
                      recordId: Long = 0L
                    )

class RetrievalDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val scheme = TableQuery[RetrievalTable]

  def insert(flow: Retrieval): Future[Retrieval] = {
    db.run((scheme returning scheme.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow)
  }

  def getById(flowId: String): Future[Option[Retrieval]] = {
    db.run(scheme.filter(flow => flow.flowId === flowId).result.headOption)
  }

  private class RetrievalTable(tag: Tag) extends Table[Retrieval](tag, "retrieval") {
    def recordId = column[Long]("record_id", O.PrimaryKey, O.AutoInc)

    def flowId = column[String]("flow_id")

    def fileName = column[String]("file_name")

    def content = column[String]("content")

    def creationDate = column[String]("creation_date")

    def * = (flowId, fileName, content, creationDate, recordId) <> (Retrieval.tupled, Retrieval.unapply)
  }

}