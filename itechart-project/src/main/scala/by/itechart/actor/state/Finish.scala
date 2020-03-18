package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.StateFinish
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Finish(
              val flowDao: FlowDao = Daos.flowDao,
              val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.finishId.id, MyDate.getCurrentDate())
            ) extends Actor with ActorLogging {
  def receive = {
    case message: StateFinish =>
      flowDao.insert(flow.copy(flowId = message.flowId))
  }
}
