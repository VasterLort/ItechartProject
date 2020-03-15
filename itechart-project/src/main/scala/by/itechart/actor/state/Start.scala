package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action._
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Start extends Actor with ActorLogging {
  def receive = {
    case message: StateStart =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.startId.id, MyDate.getCurrentDate()))
  }
}
