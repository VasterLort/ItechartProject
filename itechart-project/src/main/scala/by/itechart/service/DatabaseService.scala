package by.itechart.service

import by.itechart.action._
import by.itechart.constant.StateId
import by.itechart.dao._
import by.itechart.dao.initialization.Daos
import by.itechart.date.MyDate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseService(
                       private val flowDao: FlowDao = Daos.flowDao,
                       private val retrievalDao: RetrievalDao = Daos.retrievalDao,
                       private val transformationDao: TransformationDao = Daos.transformationDao,
                       private val retrievalService: RetrievalService = new RetrievalService(),
                       private val transformationService: TransformationService = new TransformationService(),
                       private val normalizationService: NormalizationService = new NormalizationService(),
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

  def getRetrievalFlowById(flowId: String): Future[Notice] = {
    retrievalDao.getById(flowId).map {
      case Some(res) if res.flowId == flowId => SuccessfulRequestForRetrieval(res)
      case _ => FailureRequest()
    }
  }

  def insertTransformationFlow(flow: Retrieval): Future[Notice] = {
    transformationService.getTransformedData(flow).flatMap {
      case notice: TransformedPayments =>
        transformationDao.insertAll(notice.payments).flatMap {
          case res: Seq[Transformation] =>
            insertFlow(Flow(flow.flowId, flow.fileName, StateId.transformationId.id, MyDate.getCurrentDate())).flatMap {
              case _: SuccessfulRequest => Future.successful(SuccessfulRequestForTransformation(res))
              case _ => Future.successful(FailureTransformation())
            }
          case _ => Future(FailureTransformation())
        }
      case _ => Future.successful(FailureTransformation())
    }
  }

  def getTransformationFlowById(flowId: String): Future[Notice] = {
    transformationDao.getById(flowId).map {
      case res: Seq[Transformation] => SuccessfulRequestForTransformation(res)
      case _ => FailureRequest()
    }
  }

  def insertNormalizationFlow(flow: List[Transformation]): Future[Notice] = {
    //normalizationService.getNormalizedPayment(flow)
  }

  def getNormalizationFlowById(flowId: String): Future[Notice] = {

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
