package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.{RunFinishState, RunLoadingState}
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Loading(
               val flowDao: FlowDao = Daos.flowDao,
               val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.loadId.id, MyDate.getCurrentDate())
             ) extends Actor with ActorLogging {
  def receive = {
    case message: RunLoadingState =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateLoad: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      message.statesToActor(StateId.finishId.id) ! RunFinishState(message.flowId, message.statesToActor)
  }
}