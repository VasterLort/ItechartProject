package by.itechart.dao

import by.itechart.database.DatabaseConfig
import by.itechart.database.MyPostgresProfile.api._
import org.json4s.JValue
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Transformation(
                           flowId: String,
                           fileName: String,
                           content: JValue,
                           creationDate: String,
                           recordId: Long = 0L
                         )

class TransformationDao(val dbProvider: DatabaseConfig.type = DatabaseConfig) {
  val db = dbProvider.db
  private val scheme = TableQuery[TransformationTable]

  def insert(flow: Transformation): Future[Transformation] = {
    db.run((scheme returning scheme.map(_.recordId) into ((instance, recordId) => instance.copy(recordId = recordId))) += flow)
  }

  def getById(flowId: String): Future[Option[Transformation]] = {
    db.run(scheme.filter(flow => flow.flowId === flowId).result.headOption)
  }

  private class TransformationTable(tag: Tag) extends Table[Transformation](tag, "flow_transformation") {


    def recordId = column[Long]("record_id", O.PrimaryKey, O.AutoInc)

    def flowId = column[String]("flow_id")

    def fileName = column[String]("file_name")

    def content = column[JValue]("content")

    def creationDate = column[String]("creation_date")

    def * = (flowId, fileName, content, creationDate, recordId) <> (Transformation.tupled, Transformation.unapply)
  }

}