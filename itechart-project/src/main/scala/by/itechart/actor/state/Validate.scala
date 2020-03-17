package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging, Props}
import by.itechart.action.{StateLoad, StateValidate}
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Validate(
                val flowDao: FlowDao = Daos.flowDao,
                val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.validateId.id, MyDate.getCurrentDate())
              ) extends Actor with ActorLogging {
  def receive = {
    case message: StateValidate =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateValidate: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      context.actorOf(Props(new Load()), name = message.flowId) ! StateLoad(message.flowId)
  }
}
