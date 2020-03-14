package by.itechart.state

import by.itechart.dao.Flow
import by.itechart.service.DataSavingService

import scala.concurrent.Future

object Start {
  def initFlow(): Future[Flow] = {
    new DataSavingService().insertDataFlow(Flow(1, 1, "start"))
  }
}
