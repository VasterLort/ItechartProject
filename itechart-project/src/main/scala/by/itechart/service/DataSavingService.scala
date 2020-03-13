package by.itechart.service

import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}

import scala.concurrent.Future

class DataSavingService(flowDao: FlowDao = Daos.flowDao) {
  def insertDataFlow(flow: Flow): Future[Int] = {
    flowDao.insert(flow)
  }
}
