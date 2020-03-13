package by.itechart.state

import by.itechart.dao.Flow
import by.itechart.service.DataSavingService

import scala.concurrent.Future

object Start {
  def initFlow(flow: Flow): Future[Int] = {
    val flow = Flow(1, 1, "start")
    val dataSavingService = new DataSavingService()
    val flow = dataSavingService.insertDataFlow(flow)
  }
}
