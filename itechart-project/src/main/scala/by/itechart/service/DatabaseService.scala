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
    flowDao.getById(flowId, statusId).map {
      case Some(res) if res.flowId == flowId && res.statusId == statusId => SuccessfulNotice(res)
      case _ => FailureNotice()
    }
  }

  def insertFlow(flow: Flow): Future[Notice] = {
    flowDao.insert(flow).map {
      case res: Flow => SuccessfulNotice(res)
      case _ => FailureNotice()
    }
  }
}
