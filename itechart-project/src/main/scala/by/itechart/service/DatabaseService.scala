package by.itechart.service

import by.itechart.action._
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao, Retrieval, RetrievalDao}
import by.itechart.date.MyDate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseService(
                       private val flowDao: FlowDao = Daos.flowDao,
                       private val retrievalDao: RetrievalDao = Daos.retrievalDao,
                       private val retrievalService: RetrievalService = new RetrievalService()
                     ) {

  def insertRetrievalFlow(flow: Flow): Future[Notice] = {
    retrievalService.getPaymentFile() match {
      case _: InvalidFileName => Future.successful(InvalidFileName())
      case _: EmptyFolder => Future.successful(EmptyFolder())
      case paymentFile: CsvPaymentFile =>
        retrievalDao.insert(Retrieval(flow.flowId, paymentFile.fileName, paymentFile.content, MyDate.getCurrentDate())).map {
          case res: Retrieval => SuccessfulRequestForRetrieval(res)
          case _ => FailureRetrieval()
        }
    }
  }

  def getRetrievalFlowById(flowId: String): Future[Notice] = {
    retrievalDao.getById(flowId).map {
      case Some(res) if res.flowId == flowId => SuccessfulRequestForRetrieval(res)
      case _ => FailureRequest()
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
