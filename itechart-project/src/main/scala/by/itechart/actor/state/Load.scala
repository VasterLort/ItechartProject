package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateLoad
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Load extends Actor with ActorLogging {
  def receive = {
    case message: StateLoad =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.loadId.id, MyDate.getCurrentDate()))
  }
}
