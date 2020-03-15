package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateRetrieve
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DataSavingService

class Retrieve extends Actor with ActorLogging {
  def receive = {
    case message: StateRetrieve =>
      new DataSavingService().insertDataFlow(Flow(message.flowId, StateId.retrieveId.id, MyDate.getCurrentDate()))
  }
}
