package by.itechart.service

import by.itechart.action.{FailureNotice, Notice, SuccessfulNotice}
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseService(
                       private val flowDao: FlowDao = Daos.flowDao
                     ) {

  def getFlowById(flowId: String, statusId: Long): Future[Notice] = {
    flowDao.getById(flowId, statusId).flatMap { result =>
      if (result.flowId == flowId && result.statusId == statusId) {
        Future.successful(SuccessfulNotice(result))
      } else {
        Future.successful(FailureNotice())
      }
    }
  }

  def insertFlow(flow: Flow): Future[Notice] = {
    flowDao.insert(flow).flatMap { result =>
      if (result.isInstanceOf[Flow]) {
        Future.successful(SuccessfulNotice(result))
      } else {
        Future.successful(FailureNotice())
      }
    }
  }
}
