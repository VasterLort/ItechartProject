package by.itechart.service

import by.itechart.action._
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao, Retrieval, RetrievalDao}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseService(
                       private val flowDao: FlowDao = Daos.flowDao,
                       private val retrievalDao: RetrievalDao = Daos.retrievalDao
                     ) {

  def insertRetrievalFlow(flow: Flow): Future[Notice] = {
    new RetrievalService().getPaymentFile() match {
      case _: InvalidFileName => Future.successful(InvalidFileName())
      case _: EmptyFolder => Future.successful(EmptyFolder())
      case paymentFile: CsvPaymentFile =>
        retrievalDao.insert(Retrieval(flow.flowId, paymentFile.fileName, paymentFile.content)).map {
          case res: Retrieval => SuccessfulRetrieval(res)
          case _ => FailureRetrieval()
        }
    }
  }

  def getFlowById(flowId: String, statusId: Long): Future[Notice] = {
    flowDao.getById(flowId, statusId).map {
      case Some(res) if res.flowId == flowId && res.statusId == statusId => SuccessfulRequest(res)
      case _ => FailureRequest()
    }
  }

  def insertFlow(flow: Flow): Future[Notice] = {
    flowDao.insert(flow).map {
      case res: Flow => SuccessfulRequest(res)
      case _ => FailureRequest()
    }
  }
}
