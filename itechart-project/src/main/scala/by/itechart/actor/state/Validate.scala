package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateValidate
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Validate extends Actor with ActorLogging {
  def receive = {
    case message: StateValidate =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.validateId.id, MyDate.getCurrentDate()))
  }
}
