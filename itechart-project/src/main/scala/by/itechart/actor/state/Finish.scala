package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateFinish
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Finish extends Actor with ActorLogging {
  def receive = {
    case message: StateFinish =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.finishId.id, MyDate.getCurrentDate()))
  }
}
