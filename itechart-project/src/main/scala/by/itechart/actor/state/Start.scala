package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action._
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Start(
             val flowDao: FlowDao = Daos.flowDao,
             val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.startId.id, MyDate.getCurrentDate())
           ) extends Actor with ActorLogging {
  def receive = {
    case message: RunStartState =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateStart: StatusId = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      message.statesToActor(StateId.retrieveId.id) ! RunRetrievalState(message.flowId, message.statesToActor)
  }
}
