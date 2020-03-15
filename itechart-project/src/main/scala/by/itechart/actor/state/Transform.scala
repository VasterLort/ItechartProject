package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateTransform
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Transform extends Actor with ActorLogging {
  def receive = {
    case message: StateTransform =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.transformId.id, MyDate.getCurrentDate()))
  }
}
