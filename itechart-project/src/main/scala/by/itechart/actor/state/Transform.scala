package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging, Props}
import by.itechart.action._
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Transform(
                 val flowDao: FlowDao = Daos.flowDao,
                 val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.transformId.id, MyDate.getCurrentDate())
               ) extends Actor with ActorLogging {
  def receive = {
    case message: StateTransform =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateTransform: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      context.actorOf(Props(new Normalize()), name = message.flowId) ! StateNormalize(message.flowId)
  }
}
