package by.itechart.state

import by.itechart.dao.Flow
import by.itechart.service.DataSavingService

import scala.concurrent.Future

object Retrieve {
  def updateFlow(flow: Flow): Future[Flow] = {
    new DataSavingService().insertDataFlow(flow.copy(statusId = 2, recordId = 0L))
  }
}
