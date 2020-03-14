package by.itechart.state

import by.itechart.dao.Flow
import by.itechart.service.DataSavingService

import scala.concurrent.Future

object Normalize {
  def updateFlow(flow: Flow): Future[Flow] = {
    new DataSavingService().insertDataFlow(flow.copy(statusId = 4, recordId = 0L))
  }
}
