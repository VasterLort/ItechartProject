package by.itechart.service

import by.itechart.action._
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao, Retrieval, RetrievalDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseService(
                       private val flowDao: FlowDao = Daos.flowDao,
                       private val retrievalDao: RetrievalDao = Daos.retrievalDao,
                       private val retrievalService: RetrievalService = new RetrievalService(),
                       private val transformationService: TransformationService = new TransformationService()
                     ) {

  def insertRetrievalFlow(flow: Flow): Future[Notice] = {
    retrievalService.getPaymentFile(flow.fileName) match {
      case paymentFile: CsvPaymentFile =>
        retrievalDao.insert(Retrieval(flow.flowId, paymentFile.fileName, paymentFile.content, MyDate.getCurrentDate())).flatMap {
          case res: Retrieval =>
            insertFlow(Flow(res.flowId, res.fileName, StateId.retrievalId.id, MyDate.getCurrentDate())).flatMap {
              case _: SuccessfulRequest => Future.successful(SuccessfulRequestForRetrieval(res))
              case _ => Future.successful(FailureRetrieval())
            }
          case _ => Future(FailureRetrieval())
        }
      case _ => Future.successful(FailureRetrieval())
    }
  }

  def insertTransformationFlow(flow: Retrieval): Notice = {
    transformationService.getTransformedData(flow)
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
