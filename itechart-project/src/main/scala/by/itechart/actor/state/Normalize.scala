package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateNormalize
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Normalize extends Actor with ActorLogging {
  def receive = {
    case message: StateNormalize =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.normalizeId.id, MyDate.getCurrentDate()))
  }
}
